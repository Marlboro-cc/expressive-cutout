#!/usr/bin/env python3
"""Strict source encoding checks; optionally compare every Chinese compiled APK resource."""
from pathlib import Path
import argparse
import json
import re
import subprocess
import xml.etree.ElementTree as ET
import zipfile

ROOT = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('--apk', type=Path)
parser.add_argument('--aapt2', default='aapt2')
args = parser.parse_args()
source_count = 0
chinese_files = 0
for path in ROOT.rglob('*'):
    if not path.is_file() or any(p in {'.git', '.gradle', 'build', 'local-apk'} for p in path.relative_to(ROOT).parts):
        continue
    if path.name == 'local.properties' or path.suffix not in {'.kt', '.kts', '.xml', '.json', '.md', '.py', '.sh', '.toml', '.properties', '.txt'}:
        continue
    raw = path.read_bytes()
    assert not raw.startswith(b'\xef\xbb\xbf'), f'Unexpected UTF-8 BOM: {path}'
    text = raw.decode('utf-8', errors='strict')
    assert chr(0xfffd) not in text, f'Replacement character: {path}'
    # Upstream deliberately uses two NUL separators in notificationFingerprint. Preserve them.
    nul_allowance = 2 if path.name == 'CutoutNotificationListenerService.kt' else 0
    assert text.count('\x00') <= nul_allowance, f'Unexpected NUL in source: {path}'
    if path.suffix == '.xml':
        declaration = re.match(r'<\?xml\s+[^?]*encoding=[\'"]([^\'"]+)', text)
        assert not declaration or declaration[1].lower().replace('_','-') == 'utf-8', path
        ET.fromstring(raw)
    source_count += 1
    chinese_files += bool(re.search(r'[\u3400-\u9fff]', text))

report = {'strict_utf8_source_files': source_count, 'source_files_containing_chinese': chinese_files,
          'BOM_count': 0, 'replacement_character_count': 0, 'XML_encoding_mismatches': 0}
if args.apk:
    result = subprocess.run([args.aapt2, 'dump', 'resources', str(args.apk)], check=True, stdout=subprocess.PIPE)
    table = result.stdout.decode('utf-8', errors='strict')
    zh = {}
    # AAPT2 prints embedded newlines literally, with six spaces of dump indentation.
    for block in re.split(r'(?=^    resource )', table, flags=re.M):
        key = re.match(r'    resource \S+ string/(\w+)', block)
        if not key:
            continue
        value = re.search(r'^      \(b\+zh\+Hans\) "((?:\\.|[^"\\])*)"', block, re.M)
        if value:
            quoted = '"' + value[1].replace('\n      ', '\\n') + '"'
            zh[key[1]] = json.loads(quoted)
    expected = {}
    for file in (ROOT/'app/src/main/res/values-b+zh+Hans').glob('*.xml'):
        for node in ET.parse(file).getroot():
            expected[node.get('name')] = ''.join(node.itertext()).replace('\\n','\n').replace('\\"','"').replace("\\'", "'")
    assert not expected.keys() - zh.keys(), f'Missing compiled entries: {expected.keys() - zh.keys()}'
    for key,text in expected.items():
        assert text == zh[key], f'Compiled/source mismatch: {key}'
        assert chr(0xfffd) not in zh[key], key
    with zipfile.ZipFile(args.apk) as archive:
        assert archive.testzip() is None, 'ZIP corruption'
        note = 'app/src/main/assets/changelog-zh-Hans.json'
        assert archive.read('assets/changelog-zh-Hans.json') == (ROOT/note).read_bytes()
        archive.read('assets/changelog-zh-Hans.json').decode('utf-8', errors='strict')
    report.update({'compiled_zh_resources_exactly_matched': len(expected),
                   'compiled_source_mismatches': 0, 'apk_changelog_utf8_and_bytes': 'MATCH',
                   'newline_and_android_escape_roundtrip': 'MATCH', 'apk_zip_integrity': 'PASS'})
print(json.dumps(report, ensure_ascii=False, indent=2))
