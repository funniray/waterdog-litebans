## Litebans-Waterdog  
  
Waterdog plugin that's similar to litebans.  
This plugin is still in development. If you find any issues, feel free to open an issue.  
  
### Building:  
In a command line, run
```bash
mvn package
```  
The file will be `./target/litebans-waterdodge-X.X-SNAPSHOT.jar`  
  
### Default Config:  
```yaml
# What datastore to use
# Options available: h2 (local), mysql
store: h2
jdbcurl: "jdbc:mysql://localhost:3306/litebans?allowPublicKeyRetrieval=true"
dbusername: "root"
dbpassword: "password"

#This statement may cause issues on MySQL 5.7 or MariaDB versions less than 10.2.2
#If it is, disable this option.
#This also is not supported on H2 stores. Feel free to raise a PR if you want this feature.

#See https://github.com/funniray/waterdog-litebans/issues/2
checkAlts: false

#You should change this for each proxy running the server
servername: litebans
serveruuid: f15b78eda7294493b847f54b2005dd2e

#Messages
SilentPrefix: "[Silent] "
BanMessage: >-
  You have been banned on network.
    Reason: %1$s
    Punished By: %2$s
    Duration: %3$s
  You may appeal your ban at https://example.com
BanJoinMessage: "%s tried to join, but they were banned."
BanIPJoinMessage: "%s tried to join, but they were ipbanned."
BanBroadcast: "Player %1$s was banned by %2$s for %3$s duration: %4$s"
MuteBroadcast: "Player %1$s was muted by %2$s for %3$s duration: %4$s"
WarnBroadcast: "Player %1$s was warned by %2$s for %3$s"
KickBroadcast: "Player %1$s was banned by %2$s for %3$s"
```
### Permissions:  
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