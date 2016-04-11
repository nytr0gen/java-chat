import java.util.*;

public class Nicknames {
    private static int idx = -1; // -1 value so it starts from 0

    public static String get() {
        idx = (idx + 1) % nicks.length;

        return nicks[idx]
            .toLowerCase()
            .replaceAll("[^a-z0-9_]+", " ")
            .trim()
            .replaceAll("\\s+", "_");
    }

    private static String[] nicks = {
        "Scooby-Dum",
        "Bambi",
        "Boo-Boo Bear",
        "Louie",
        "Underdog",
        "George Jetson",
        "Quick Draw McGraw",
        "Velma Dinkley",
        "Henery Hawk",
        "Blacque Jacque Shellacque",
        "Atom Ant",
        "Grape Ape",
        "Captain Caveman",
        "Lucy van Pelt",
        "Darkwing Duck",
        "Magica De Spell",
        "Top Cat",
        "Space Ghost",
        "Felix the Cat",
        "Magilla Gorilla",
        "Fred Jones",
        "Wally Gator",
        "Jiminy Cricket",
        "Eeyore",
        "Scrooge McDuck",
        "Spike Bulldog",
        "Mighty Mouse",
        "Huey",
        "Dewey",
        "Muttley",
        "Hugo the Abominable Snowman",
        "Chip",
        "Dale",
        "Charlie Brown",
        "Homer Simpson",
        "Snagglepuss",
        "Penelope Pitstop",
        "The Powerpuff Girls",
        "Betty Boop",
        "Fred Flintstone",
        "Mr. Magoo",
        "Barney Rubble",
        "Dopey",
        "Boris Badenov",
        "Natasha Fatale",
        "Penelope Pussycat",
        "Bart Simpson",
        "Superman",
        "Linus van Pelt",
        "Cruella De Vil",
        "Pluto",
        "Granny"
    };
}
