# ~~New~~ Ultra Rapid Spectate

### ~~Do you think new NURF mode is too slow? Do you want to see the games but don't have 8hours to spare? With this app you can watch your favorite moments from NURF in quick way cutting down from 8 hours to just 48 minutes of exciting and strategic gameplay!~~

### Well this app will make URF game even faster. Because why not!

![main screen](https://github.com/knezzz/URS/blob/master/screenShoots/Screenshot_2015-04-17-22-21-38.png)

![map view start](https://github.com/knezzz/URS/blob/master/screenShoots/Screenshot_2015-04-17-22-21-44.png)

![map view 12 minutes](https://github.com/knezzz/URS/blob/master/screenShoots/Screenshot_2015-04-17-22-23-00.png)

![map view won!](https://github.com/knezzz/URS/blob/master/screenShoots/Screenshot_2015-04-17-22-24-00.png)

#### _Android app that will show roughly how the game was played in 10x speed_

##### The app can be downloaded here: [Dropbox](https://www.dropbox.com/s/00cievleaohu3m4/UltraRapidSpectate.apk?dl=0)

#### How does it work:
 **Map**:
Map is canvas that got bitmap of map painted over it. Canvas is then scaled to match image size. Events are added from data I got from API. Event positions are then scaled down to fit the map (since canvas can't be larger than mobile screen).
Map has all the game details, from Players in game, towers and events that will occur in next minute.
Above map there is game time, it's speed up and it matches all the events happening on map.
Below the map we have timeline, here all the champions, dragon, baron, turret, inhibitor kills are shown. There is logic for champion multikills so that is shown here as well, except "30 seconds to Penta Kill after a Quadra Kill if no enemy respawns." this part since I didn't implement respawn event. In this app you have 10 seconds for Penta If it took you longer then don't show off you play with this app :). Events are passed in form (killer name, killer team color, event[gray, changes color only on dragon, baron kill], victim name, victim team color, multikill [gray]); killer name is used if champion got executed "(name) has been executed", but instead of killer victim is passed.
Upon creating SpectateMain where map is located, map takes all the Summoners from game and gets their square icon, resizes it, cuts it to circle and adds border depending on team color.

**Inhibitor** **Tower** **Baron** **Dragon**
Those classes are pretty much the same. They have private attributes and getters, some setters. Inhibitor, Baron and Dragon have respawn time in milliseconds.
Baron and Dragon also have icon there because they don't have state, icon is there or not. Inhibitor and Tower have 3 states so I think that would use too much memory.

TODO:
- Add dynamic loading of the matches.
- Add time slider so user can use epoch time.
- Add tower, inhibitor, nexus, champions in game and ward artwork.
- Add wall detection and algorithm for path finding. (from snake?!)
- Detection for Penta kill (for now it's giving just 10 seconds)
- Dragon, baron tower counter.
- Add going to base with "b", and teleporting logic (possibly also add ezreal, jinx, ashe and other global ulti kills as well).
- Add ward position (roughly calculated based on current player position) algorithm - get creator position, get killer position, see if there is bush near by....
- Add ward timer (so ward will be destroyed once timer gets to 0)
- Destroy ward function (just remove ward from arrayList)
- Fog of war possibly?!

## What is Ultra Rapid Spectate
#### Since Ultra Rapid Fire is fast paced game with a lot of killing going on, I thought it will be good idea to show that games on mobile device.

### App was made for URF but with few tweaks it can be reorganized to show match-history:
TODO for match-history tweak:
- Add maps support
- Add CAPTURE_POINT and other necessary data points.
- Change home screen to show players history instead of whole match
