# Changelog

### Version 1.19.4 - 1.3
 - port to 1.19.4

### Version 1.19.3 - 1.3
- port to 1.19.3
- move config to json format to have the same config on forge and fabric
- add command `/healthcommand`
    - `/healthcommand github` shows the url to the github page
    - `/healthcommand discord` shows the url to the discord server
    - `/healthcommand wiki` shows the url to the wiki
    - `/healthcommand issues` shows the url to the issues page
    - `/healthcommand curseforge` shows the url to the curseforge page
    - `/healthcommand modrinth` shows the url to the modrinth page
    - `/healthcommand config`
        - `/healthcommand config reload` reloads the config
        - `/healthcommand config show` shows the config
        - `/healthcommand config reset` resets the config to its default values

### Version 1.19.1 - 1.2
- port to 1.19.1

### Version 1.19 - 1.2
- port to 1.19

### Version 1.18.1 - 1.2
- port to 1.18.1

### Version 1.17.1 - 1.2
- move config file to normal forge-config-folder

### Version 1.17.1 - 1.1
- added new subcommand `/health reset <targets>`
    - will reset the maximum health of the entity to the previous base value
    - `/health set <targets> <amount>` will now reduce the maximum health of the targets to the set amount

### Version 1.17.1 - 1.0
- added `/health` command
