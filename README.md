TextFeed2Images
===============

Create images from a RSS feed for viewing via Kodi picture slideshow. 

Parameters:

* q - Feed URL 
* backgroundColor - (Optional) Hex value for background color. Example AFE3CD
* foregroundColor - (Optional) Hex value for foreground color. Example A0BC35
* transparency - (Optional) Transparency factor of background color. Range 1 to 255. Example: 80
* destroy - (Optional) Destroy cached images. Please only use during debugging. Example: yes
* invert - (Optional) Invert the default black on white to white on black. Example: yes

Example URLs:

```
http://xbmc-rocks.com/go?q=http://feeds.reuters.com/reuters/topNews
```

```
http://xbmc-rocks.com/go?q=http://feeds.reuters.com/reuters/topNews&invert=yes&destroy=yes
```

```
http://xbmc-rocks.com/go?q=http://feeds.reuters.com/reuters/topNews&transparency=30&foregroundColor=aabbcc&backgroundColor=bbbbbb&destroy=yes
```
