# Changelog

## Version 1.16.5 - 1.5.0

- config system parsing now based on vanilla codecs
- removed command `/healthcommand config show`
- changed command `/health get <targets>`
    - function returns the health of the target, if only one is selected
    - function returns the number of selected targets, if more than one is selected
- changed command `/health set <targets> <health>` and `/health add <targets> <amount>`
    - amount can now be a float instead of an integer
- added optional argument `<updateMaxHealth>` to `/health add`
- added optional argument `<updateMaxHealth>` to `/health set`
- added new config option `fireLivingHealEvent`
    - When using the command `/health add`, and the health of an entity increases, should the `LivingHealEvent` be
      fired?
- Changed how `/health add` and `/health set` behave
    - Full desciption on the new [Wiki](https://github.com/tristankechlo/HealthCommand/wiki)
- remove command options `goBeyondMaxHealthForAdding` and `goBeyondMaxHealthForSetting` as they are replaced by the
  command arhuments

## Version 1.16.5 - 1.4.1

- remove translation files
    - now displays proper messages when only installed on the server

## Version 1.16.5 - 1.3

- move config to json format to have the same config on forge and fabric
- add command `/healthcommand`
    - `/healthcommand github` shows the url to the github page
    - `/healthcommand discord` shows the url to the discord server
    - `/healthcommand issues` shows the url to the issues page
    - `/healthcommand curseforge` shows the url to the curseforge page
    - `/healthcommand modrinth` shows the url to the modrinth page
    - `/healthcommand config`
        - `/healthcommand config reload` reloads the config
        - `/healthcommand config show` shows the config
        - `/healthcommand config reset` resets the config to its default values

## Version 1.16.5 - 1.2

- move config file to normal forge-config-folder

## Version 1.16.5 - 1.1

- added new subcommand `/health reset <targets>`
    - will reset the maximum health of the entity to the previous base value
    - `/health set <targets> <amount>` will now reduce the maximum health of the targets to the set amount

## Version 1.16.5 - 1.0

- added `/health` command
