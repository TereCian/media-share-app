## media-share-app

Massive thanks to [JavaTube](https://github.com/felipeucelli/JavaTube) for open sourcing their code, as it is currently used both to search for videos, as well as loading video data.
Hopefully this will no longer be necessary with full releases of VLC 4.

This also likely breaks YouTubes TOS, as it displays the video outside their proprietary player. Please do not smite me, Google.

### General usage:

The app consists of two parts: a local part displaying the media and a remote part serving as the public interface for queue control.
Both parts can run on the same machine, but don't have to. 
To ease running both on the same machine, both parts use the same jar and the mode of operation is determined through a commandline parameter.

### Local Half:
Prerequisites:

- Java 25
- An Installation of VLC 3 somewhere on the machine
- Port 8080 free for use

The parameter for local use is `-Dspring.profiles.active=local`.
An example run command could look like this: `java -jar -Dspring.profiles.active=local media-share-app.jar`.

When starting the app, it will open a configuration and control page in your default browser, where further configuration can take place. 
If the page does not open automatically, you can open it yourself at http://localhost:8080/index.

### Remote Half:
Prerequisites:

- Java 25
- Port 8081 free for use

The parameter for local use is `-Dspring.profiles.active=remote`.
An example run command could look like this: `java -jar -Dspring.profiles.active=remote media-share-app.jar`.

Upon first start of the app, you will also have to add the `-Dpassword=<password>` parameter.
This password can then be entered through the configuration interface on the local half to access settings for the remote half.
The password is hashed and saved to a file, so the password does not need to be re-entered when restarting the remote app.

### Building the jar locally

To build the jar yourself, you can use the bootJar gradle task.
This can be done by using the `./gradlew :bootJar` command in the main directory of this project.
The resulting jar will be placed in the build/lib/ directory.