#!/usr/bin/env python3
"""Offline localization coverage/format regression checks (stdlib only)."""
from pathlib import Path
import json
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'app/src/main/res'

def read(folder):
    entries = {}
    for file in sorted((RES / folder).glob('*.xml')):
        for node in ET.parse(file).getroot():
            if node.tag not in ('string', 'plurals', 'string-array'):
                continue
            name = node.attrib['name']
            assert name not in entries, f'Duplicate resource {name}'
            entries[name] = node
    return entries

def tokens(text):
    return sorted(re.findall(r'%(?:\d+\$[-#+ 0,(]*\d*(?:\.\d+)?[a-zA-Z]|%)', text or ''))

base = read('values')
zh = read('values-b+zh+Hans')
required = {k: v for k, v in base.items() if v.get('translatable') != 'false'}
assert not required.keys() - zh.keys(), f'Missing: {required.keys() - zh.keys()}'
assert not zh.keys() - base.keys(), f'Unknown: {zh.keys() - base.keys()}'
assert not {k for k,v in base.items() if v.get('translatable') == 'false'} & zh.keys()
for key, source in required.items():
    target = zh[key]
    assert source.tag == target.tag, key
    assert ''.join(target.itertext()).strip(), f'Empty: {key}'
    assert tokens(''.join(source.itertext())) == tokens(''.join(target.itertext())), f'Format mismatch: {key}'
    if source.tag == 'plurals':
        assert any(item.get('quantity') == 'other' for item in target), key
locales = [n.get('{http://schemas.android.com/apk/res/android}name') for n in ET.parse(RES / 'xml/locales_config.xml').getroot()]
assert len(locales) == len(set(locales)) and 'zh-Hans' in locales
assets = ROOT / 'app/src/main/assets'
en_notes = json.loads((assets / 'changelog.json').read_text())
zh_notes = json.loads((assets / 'changelog-zh-Hans.json').read_text())
assert len(en_notes) == len(zh_notes)
notes_count = 0
for en, cn in zip(en_notes, zh_notes):
    assert en['version'] == cn['version']
    assert cn['description'] and cn['description'] != en['description']
    notes_count += 1
    for section in ('features', 'bugfixes'):
        assert len(en[section]) == len(cn[section])
        assert all(a and a != b for a,b in zip(cn[section],en[section]))
        notes_count += len(cn[section])
# Stable icon IDs must remain machine-readable; only separate labels are translated.
catalog = (ROOT / 'app/src/main/java/com/ekoehler/expressivecutout/overlay/MaterialIconCatalog.kt').read_text()
keys = re.findall(r'MaterialIconOption\("([^"]+)"',catalog)
assert len(keys) == len(set(keys)) == 61
assert all('icon_label_' + k in zh for k in keys)
resolver = (ROOT / 'app/src/main/java/com/ekoehler/expressivecutout/overlay/IconResolver.kt').read_text()
assert all('"' + k + '"' in resolver for k in ('answer', 'decline', 'hang up', '接听', '拒接', '挂断'))
identical = [k for k in required if ''.join(required[k].itertext()) == ''.join(zh[k].itertext())]
print(json.dumps({'translatable_resources': len(required), 'zh_resources': len(zh),
                  'missing_resources': 0, 'format_mismatches': 0, 'changelog_entries': len(zh_notes),
                  'changelog_texts': notes_count, 'icon_labels': len(keys),
                  'intentionally_unchanged_brand_or_data_strings': identical}, ensure_ascii=False, indent=2))
