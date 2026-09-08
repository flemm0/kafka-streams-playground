To fix annoying import not found errors:

1. Refresh the Bloop configuration (regenerates the files under `.bloop/`):
```bash
mvn generate-sources ch.epfl.scala:bloop-maven-plugin:2.0.5:bloopInstall
```

2. Open VSCode Command Palette

3. Run `Metals: Restart Build Server`

4. If needed, run `Metals: Import Build`

---

To run a Java shell session with the `datafaker` library already loaded, use `JShell` with `JBang`:
```bash
jbang -i net.datafaker:datafaker:2.7.0
```
