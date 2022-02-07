## Litebans-Waterdog  
  
Waterdog plugin that's similar to litebans.  
This plugin is still in development. If you find any issues, feel free to open an issue.  
  
### Building:  
In a command line, run
```bash
mvn package
```  
The file will be `./target/litebans-waterdodge-X.X-SNAPSHOT.jar`  
  
### permissions:  
Please note, some permissions values may be missing, however, these permissions should be similar to normal liteban's permissions.
```yaml
Mod permissions:
  - litebans.base # Required for mods to run commands from lbwd

  - litebans.ban
  - litebans.tempban
  - litebans.tempbanip
  - litebans.unban.own # Required to unmute in general
  - litebans.unban # Required to unban anyone -- mods requires litebans.unban.own still!

  - litebans.mute
  - litebans.tempmute
  - litebans.tempmuteip
  - litebans.unmute.own # Required to unmute in general
  - litebans.unmute # Required to unmute anyone -- mods requires litebans.unmute.own still!

  - litebans.kick

  - litebans.warn

  - litebans.notify # Includes everything below
  - litebans.notify.silent
  - litebans.notify.dupeip
  - litebans.notify.banned_join

Player permission (optional):
  - litebans.notify.broadcast
  - litebans.notify.warned
  - litebans.notify.warned.offline
```