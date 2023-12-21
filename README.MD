# Replace field accesses with method calls

This POC uses ByteBuddy (https://bytebuddy.net/) to replace field accesses with method calls.
It uses ASM (https://asm.ow2.io/) to parse the class files and ByteBuddy to generate the new class files.
The goal is to replace something like this:

```java
public class Test {
    private int i;
    public void readI() {
        System.out.println(this.i);
    }
}
```

with something like this:

```java
public class Bootstrap {
    public static int getI(Test test) {
        return ... ;
    }
}

public class Test {
    private int i;
    public void readI() {
        System.out.println(Bootstrap.getI());
    }
}
```