-injars 'dist/temp/JFireWare-Client.jar'
-injars 'dist/temp/JFireWare-Server.jar'
-outjars 'dist/final/'

-libraryjars <java.home>/lib/rt.jar
-libraryjars lib

-printmapping mapping.log

# The following annotations can be specified with classes and with class
# members.
# @Keep specifies not to shrink, optimize, or obfuscate the annotated class
# or class member as an entry point.
-keep @proguard.annotation.Keep class *

-keepclassmembers class * {
    @proguard.annotation.Keep
    <fields>;
    @proguard.annotation.Keep
    <methods>;
}

# The following annotations can only be specified with classes.
# @KeepImplementations and @KeepPublicImplementations specify to keep all,
# resp. all public, implementations or extensions of the annotated class as
# entry points. Note the extension of the java-like syntax, adding annotations
# before the (wild-carded) interface name.
-keep class * extends @proguard.annotation.KeepImplementations *

-keep public class * extends @proguard.annotation.KeepPublicImplementations *

# @KeepApplication specifies to keep the annotated class as an application,
# together with its main method.
-keepclasseswithmembers @proguard.annotation.KeepApplication public class * {
    public static void main(java.lang.String[]);
}

# @KeepClassMembers, @KeepPublicClassMembers, and
# @KeepPublicProtectedClassMembers specify to keep all, all public, resp.
# all public or protected, class members of the annotated class from being
# shrunk, optimized, or obfuscated as entry points.
-keepclassmembers @proguard.annotation.KeepClassMembers class * {
    <fields>;
    <methods>;
}

-keepclassmembers @proguard.annotation.KeepPublicClassMembers class * {
    public <fields>;
    public <methods>;
}

-keepclassmembers @proguard.annotation.KeepPublicProtectedClassMembers class * {
    public protected <fields>;
    public protected <methods>;
}

# @KeepGettersSetters and @KeepPublicGettersSetters specify to keep all, resp.
# all public, getters and setters of the annotated class from being shrunk,
# optimized, or obfuscated as entry points.
-keepclassmembers @proguard.annotation.KeepGettersSetters class * {
    void set*(***);
    void set*(int,***);
    boolean is*();
    boolean is*(int);
    *** get*();
    *** get*(int);
}

-keepclassmembers @proguard.annotation.KeepPublicGettersSetters class * {
    public void set*(***);
    public void set*(int,***);
    public boolean is*();
    public boolean is*(int);
    public *** get*();
    public *** get*(int);
}

# @KeepName specifies not to optimize or obfuscate the annotated class or
# class member as an entry point.
-keep,allowshrinking @proguard.annotation.KeepName class *

-keepclassmembers,allowshrinking class * {
    @proguard.annotation.KeepName
    <fields>;
    @proguard.annotation.KeepName
    <methods>;
}

# @KeepClassMemberNames, @KeepPublicClassMemberNames, and
# @KeepPublicProtectedClassMemberNames specify to keep all, all public, resp.
# all public or protected, class members of the annotated class from being
# optimized or obfuscated as entry points.
-keepclassmembers,allowshrinking @proguard.annotation.KeepClassMemberNames class * {
    <fields>;
    <methods>;
}

-keepclassmembers,allowshrinking @proguard.annotation.KeepPublicClassMemberNames class * {
    public <fields>;
    public <methods>;
}

-keepclassmembers,allowshrinking @proguard.annotation.KeepPublicProtectedClassMemberNames class * {
    public protected <fields>;
    public protected <methods>;
}
