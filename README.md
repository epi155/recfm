# recfm-maven-plugin

## Table of Contents

* [1. Introduction](#1)
* [2. Preview](#2)
* [3. Plugin parameters details](#3)
* [4. Configuration YAML details](#4)
    * [4.1. Package level](#41)
    * [4.2. Class level](#42)
    * [4.3. Field level](#43)
        * [4.3.1. Alphanumeric](#431)
        * [4.3.2. Numeric](#432)
        * [4.3.3. Filler](#433)
        * [4.3.4. Constant](#434)
        * [4.3.5. Group](#435)
        * [4.3.6. Occurs](#436)
* [5. Special methods](#5)
    * [5.1. `static ... decode(String s)`](#51)
    * [5.2. `String encode()`](#52)
    * [5.3. `String toString()`](#53)
    * [5.4. Validation method](#54)

## <a name="1">1. Introduction</a>

To save the contents of a class to a file, the simplest thing is to convert individual fields to strings and concatenate
all strings into one string which becomes the record of the file. For the reverse operation it is necessary to break the
string associated with the file record and convert the substrings into the individual fields. In order to convert
individual fields into strings and vice versa, it is necessary to provide some additional information, the length on
file of the individual fields, and the rules for alignment, filling and normalization.

The classes generated by this plugin have an approach particular: the data of the class is hosted by a vector of
characters. When the class is created from the string associated with the file record, the character vector is simply
set with the character vector representing the string. When the class is converted to the associated record string of
the file, the character vector of the class is transformed into the string. Getters and setters read and write directly
on the fragment of the character vector corresponding to that field.

Numeric fields are handled as strings that accept only numeric characters. for these fields it is also possible to
request the generation of getters and setters with primitive numeric types.

## <a name="2">2. Preview</a>

Configuration plugin example:

~~~xml
    <plugin>
      <groupId>io.github.epi155</groupId>
      <artifactId>recfm-maven-plugin</artifactId>
      <version>0.5.5</version>
      <configuration>
        <settings>
          <setting>recfm-suez.yaml</setting>
        </settings>
      </configuration>
      <executions>
        <execution>
          <goals>
            <goal>fixed</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
~~~

Configuration file example:

~~~
packageName: org.example.sys.file
classes:
  - name: SuezBody
    len: 543
    fields:
      - !Num { name: ibrKey  , at:   1, len:  11 }
      - !Num { name: ibrPrg  , at:  12, len:   6 }
      - !Num { name: recPrg  , at:  18, len:   9 }
      - !Abc { name: funcData, at:  27, len: 500 }
      - !Abc { name: reseData, at: 527, len:  17 }
~~~

Commandline

~~~
mvn recfm:fixed
~~~

Generated java class

~~~java
package org.example.sys.file;
...
public class SuezBody extends FixRecord {
    public static final int LRECL = 543;
    public SuezBody() { ... }
    public static SuezBody decode(String s) { ... }
    public String encode();
    ...
    public String getIbrKey() { ... }
    public void setIbrKey(String s) { ... }
    
    public String getIbrPrg() { ... }
    public void setIbrPrg(String s) { ... }
    
    public String getRecPrg() { ... }
    public void setRecPrg(String s) { ... }
    
    public String getFuncData() { ... }
    public void setFuncData(String s) { ... }
    
    public String getReseData() { ... }
    public void setReseData(String s) { ... }

    public String toString() { ... }
}
~~~

## <a name="3">3. Plugin parameters details</a>

Parameters

`outputUtilPackage`
: Indicates the name of the package under which to generate the utility classes, if omitted it is necessary to include
in the dependencies the library linked to the plugin **recfm-lib-java**

`outputSourceDirectory`
: Indicates the base directory from which to generate packages, default value is `${project.build.sourceDirectory}`,
ie **`src/main/java`**

`settingsDirectory`
: Indicates the base directory that contains the configuration files, default value
is `${project.build.resources[0].directory}`, ie **`src/main/resources`**

**`settings`**
: List of configuration files (required).

`doc`
: Indicates whether or not to generate the javadoc documentation on setters and getters, default is **`false`**.

`align`
: Indicates the minimum alignment of numeric fields when numeric representation is required. The Default value is **4**,
ie setters and getters are adapted to `int` or `long` depending on the length of the field. Using 2 also uses `short`,
and using 1 also uses `byte`.

## <a name="4">4. Configuration YAML details</a>

In general we can have multiple configuration files.
Each configuration file can define multiple classes within a package.

### <a name="41">4.1. Package level</a>

The first thing to define is the package name

~~~yml
packageName: org.example.sys.file
~~~

Then we can assign some predefined behaviors, these behaviors can also be defined at the field level, in this case they
are defined at the package level

~~~yml
defaults:
  fillChar: 0
  check: Ascii
~~~

`defaults.fillChar` is the fill character to use for fields of type *Filler*, the default value is **0** (ie **\u0000**)
.
`defaults.check` indicates which checks to perform on alphanumeric fields, the default value is **Ascii**.

After that we can define the single classes

~~~yml
classes:
  - { ... }
~~~

### <a name="42">4.2. Class level</a>

A string is associated with each class. At the class level the following parameters can be defined:

~~~yml
classes:
  - name: SuezBody
    length: 543
    onOverflow: Trunc
    onUnderflow: Pad
    fields:
      - { ... }
~~~

* `classes[].name` is the name of the class.
* `classes[].length` is the length of the string (`length` can be abbreviated to `len`)
* `classes[].onOverflow` indicates how to behave in the deserialization phase if the length of the supplied string is
  greater than that expected, default value is **Trunc** (ie extra characters are ignored), the alternate value is **
  Error** that throws a *RecordOverflowException*.
* `classes[].onUnderflow` indicates how to behave in the deserialization phase if the length of the supplied string is
  less than the expected one, default value is **Pad** (ie ..), the alternate value is **Error** that throws a *
  RecordUnderflowException*.

### <a name="43">4.3. Field level</a>

It is possible to define various types of fields, each of which has specific attributes. For this reason a specific json
tag is used for each type, Let's see them in detail one by one.

The entire area of the class or of a group of it must be associated with a field. If a part of the area is not needed it
can be defined as a filler.

#### <a name="431">4.3.1. Alphanumeric </a>

Tag for alphanumeric field is `Abc`, the possible attributes are:

|attribute  |alt| type  |note                            |
|-----------|---| :---: |--------------------------------|
|[offset](#fld.offset)   |at | int   | **required**                   |
|[length](#fld.length)   |len| int   | **required**                   |
|[name](#fld.name)       |   |String | **required**                   |
|[redefines](#fld.redef) |red|boolean| default `false`                |
|[audit](#fld.audit)     |   |boolean| default `false`                |
|[onOverflow](#fld.ovfl) |   |[^1]   | default `TruncRight`           |
|[onUnderflow](#fld.unfl)|   |[^2]   | default `PadRight`             |
|[padChar](#fld.pchr)    |   |char   | default value `' '`            |
|[check](#fld.chk)       |   |[^3]   | default value `defaults.check` |

[^1]: Overflow domain: TruncRight, TruncLeft, Error
[^2]: Underflow domain: PadRight, PadLeft, Error
[^3]: Check domain: None, Ascii, Latin1, Valid

Some attributes also have a shortened form. The meaning of some attributes is immediate.
The <a name='fld.offset'>offset</a> attribute indicates the starting position of the field (starting from 1).
The <a name='fld.length'>length</a> attribute indicates the length of the field.
The <a name='fld.name'>name</a> attribute indicates the name of the field.

~~~yml
classes:
  - name: Foo
    length: 75
    fields:
      - !Abc { name: huey , at:  1, len: 25 }
      - !Abc { name: dewey, at: 26, len: 25 }
      - !Abc { name: louie, at: 51, len: 25 }
~~~

Generated setter and getter

~~~java
    public String getHuey(){...}
    public void setHuey(String s){...}

    public String getDewey(){...}
    public void setDewey(String s){...}

    public String getLouie(){...}
    public void setLouie(String s){...}
~~~

The <a name='fld.redef'>redefines</a> attribute indicates that the field is a redefinition of an area, this field will
not be considered in the overlay checks

~~~yml
  - name: Foo
    length: 10
    fields:
      - !Abc { name: isoDate , at: 1, len: 10 }   # yyyy-MM-dd
      - !Num { name: year    , at: 1, len:  4, red: true }
      - !Num { name: month   , at: 6, len:  2, red: true }
      - !Num { name: day     , at: 9, len:  2, red: true }
~~~

To introduce the <a name='fld.audit'>audit</a> attribute see section [5.4](#54).

<a name='fld.ovfl'>onOverflow</a> indicates what to do if you try to set a value whose length is greater than the
defined one. It is possible to set `TruncLeft`, `TruncRight` and `Error`, in the first two cases the value is truncated,
respectively to the left or to the right, in the last case an exception is thrown.

<a name='fld.unfl'>onUnderflow</a> indicates what to do if you try to set a value whose length is less than the defined
one. It is possible to set `PadLeft`, `PadRight` and `Error`, in the first two cases the value is padded respectively to
the left or to the right, in the last case an exception is thrown.

<a name='fld.pchr'>padchar</a> indicates the character to use for padding, in case of underflow.

<a name='fld.chk'>check</a> indicates which checks to perform in the *validate* or *audit* phase. The following values
are available:

`None`
: no check will be performed

`Ascii`
: only ascii characters are accepted, control characters are not accepted (95 characters only)

`Latin1`
: only latin1 (ISO-8859-1) characters are accepted, control characters are not accepted (190 characters only)

`Valid`
: characters that pass the `Character.isDefined(c)` test are accepted

#### <a name="432">4.3.2. Numeric </a>

Tag for numeric field is `Num`, many attributes have the same meaning as in the alphanumeric case, the padding character
is necessarily 0, the control is necessarily that the characters are numeric, the possible attributes are:

|attribute  |alt|type   |note                            |
|-----------|---| :---: |--------------------------------|
|[offset](#fld.offset)   |at | int   | **required**                   |
|[length](#fld.length)   |len| int   | **required**                   |
|[name](#fld.name)       |   |String | **required**                   |
|[redefines](#fld.redef) |red|boolean| default `false`                |
|[audit](#fld.audit)     |   |boolean| default `false`                |
|[onOverflow](#fld.ovfl) |   |[^1]   | default `TruncLeft`            |
|[onUnderflow](#fld.unfl)|   |[^2]   | default `PadLeft`              |
|[numericAccess](#fld.num)|num|boolean| default value `false`        |

<!**
|[space](#fld.spc)      | |[^4]   | default value `Deny` |

[^4]: Space domain: Deny, Null, Init
-->

<a name='fld.num'>numericAccess</a> indicates whether to generate the numeric setters and getters for the field, in
addition to the alphanumeric ones. Numeric getters are prefixed with the return type.

~~~yml
  - name: Foo
    length: 10
    fields:
      - !Num { name: year , at: 1, len: 4, num: true }
      - !Fil {              at: 5, len: 1 }
      - !Num { name: month, at: 6, len: 2, num: true }
      - !Fil {              at: 8, len: 1 }
      - !Num { name: date , at: 9, len: 2, num: true }
~~~

Generated java for *year* field

~~~java
    public String getYear(){...}
    public void setYear(String s){...}
    public int intYear(){...}
    public void setYear(int n){...}
~~~

<!--
<a name='fld.spc'>space</a> is a force to handle a null value. The following values are available:

`Deny`
: the field must be strictly numeric (no SPACES allow)

`Null`
: the field can also be SPACES, it is read as null, it is initialized to ZEROES

`Init`
: the field can also be SPACES, it is read as null, it is initialized to SPACES
-->

#### <a name="433">4.3.3. Filler </a>

Tag for filler field is `Fil`, a filler is an area we are not interested in, neither getters nor setters are generated
for it, the possible attributes are:

|attribute  |alt|type   |note                            |
|-----------|---| :---: |--------------------------------|
|[offset](#fld.offset)  |at | int   | **required**                   |
|[length](#fld.length)  |len| int   | **required**                   |
|[fillChar](#fld.fill)  |   |char   | default value `defaults.fillChar`|
|[check](#fld.chk)      |   |[^3]   | default value `defaults.check` |

<a name='fld.fill'>fillChar</a> indicates the character to use to initialize the area

#### <a name="434">4.3.4. Constant </a>

Tag for constant field is `Val`, even for a constant field the setters and getters are not generated, the controls
verify that the present value coincides with the set one, the possible attributes are:

|attribute  |alt|type   |note                            |
|-----------|---| :---: |--------------------------------|
|[offset](#fld.offset)  |at | int   | **required**                   |
|[length](#fld.length)  |len| int   | **required**                   |
|[value](#fld.val)      |val|String | **required**                   |
|[audit](#fld.audit)    |   |boolean| default `false`                |

<a name='fld.val'>value</a> indicates the value with which to initialize the area

#### <a name="435">4.3.5. Group </a>

Tag for group field is `Grp`, a group allows you to group multiple fields in order to structure the area, the possible
attributes are:

|attribute  |alt|type   |note                            |
|-----------|---| :---: |--------------------------------|
|[offset](#grp.offset)   |at | int   | **required**                   |
|[length](#grp.length)   |len| int   | **required**                   |
|[name](#grp.name)       |   |String | **required**                   |
|[redefines](#grp.redef) |red|boolean| default `false`                |
|[fields](#grp.flds)     |   |array  | **required** child fields      |

The <a name='grp.offset'>offset</a> attribute indicates the starting position of the group (starting from 1).
The <a name='grp.length'>length</a> attribute indicates the length of the group.
The <a name='grp.name'>name</a> attribute indicates the name of the group.
The <a name='grp.redef'>redefines</a> attribute indicates that the group is a redefinition of an area, this group will
not be considered in the overlay checks
The <a name='grp.flds'>fields</a> attribute indicates a definition list of fields

Group definition example:

~~~yml
  - name: B280v2xReq
    len: 19324
    fields:
      - !Grp { name: transactionArea     , at:  1, len: 12,
               fields: [
               !Abc { name: cdTransazione, at:  1, len: 9 },
               !Num { name: esitoAgg     , at: 10, len: 1 },
               !Num { name: esitoCompl   , at: 11, len: 1 },
               !Val { val: "\n"          , at: 12, len: 1 }
               ]}
      - ...
~~~

Group usage example:

~~~java
        val b280=new B280v2xReq();
        b280.transactionArea().setCdTransazione("TR00");
        b280.transactionArea().setEsitoAgg("0");
        val esitoComplTransaction=b280.transactionArea().getEsitoCompl();
~~~

#### <a name="436">4.3.6. Occurs </a>

Tag for occurs field is `Occ`, an occurs is basically a repeated group, it is defined with the group data of the first
occurrence and the number of occurrences, the possible attributes are:

|attribute  |alt|type   |note                            |
|-----------|---| :---: |--------------------------------|
|[offset](#occ.offset)   |at | int   | **required**                   |
|[length](#occ.length)   |len| int   | **required**                   |
|[name](#occ.name)       |   |String | **required**                   |
|[redefines](#occ.redef) |red|boolean| default `false`                |
|[fields](#occ.flds)     |   |array  | **required** child fields      |
|[times](#occ.times)     |x  | int   | **required** occurrences       |

The <a name='occ.offset'>offset</a> attribute indicates the starting position of the first group (starting from 1).
The <a name='occ.length'>length</a> attribute indicates the length of a single group.
The <a name='occ.name'>name</a> attribute indicates the name of the group.
The <a name='occ.redef'>redefines</a> attribute indicates that the group is a redefinition of an area, this group will
not be considered in the overlay checks
The <a name='occ.flds'>fields</a> attribute indicates a definition list of fields, the offsets of the fields are those
relative to the first group
The <a name='occ.times'>times</a> attribute indicates the number of times the group is repeated

Occurs definition example:

~~~yml
  - name: FooResp
    len: ...
    fields:
      - ...
      - !Occ { name: errItem                  , at:  92, len:   20, x: 25,
               fields: [
                 !Abc { name: applicationId   , at:  92, len:    2},
                 !Abc { name: errorCodeSource , at:  94, len:    5},
                 !Abc { name: errorCodeTarget , at:  99, len:    4},
                 !Abc { name: aliasId         , at: 103, len:    8},
                 !Fil {                         at: 111, len:    1}
               ] }
      - ...
~~~

Occurs usage example:

~~~java
        val resp=new FooResp();
        ...
        resp.errItem(1).setApplicationId("05");
        resp.errItem(1).setErrorCodeSource("91302");
        resp.errItem(2).setApplicationId("07");
        resp.errItem(2).setErrorCodeSource("38000");
~~~

## <a name="5">5. Special methods</a>

In addition to the setters and getters, the deserialization, serialization and dump methods of the class are defined

## <a name="51">5.1. `static ... decode(String s)`</a>

This method is used to create the class from a string. The content of the string is not automatically validated it is
advisable to validate the class with the appropriate methods before using the class. The class can also be instantiated
using the empty constructor, in this case all fields are initialized to default values.

## <a name="52">5.2. `String encode()`</a>

`encode` is the serialization methods, it transforms the class into the string that represents it.

## <a name="53">5.3. `String toString()`</a>

The toString method is used to dump the class. The list of fields is shown and for each field the offset, length and
value are shown.

## <a name="54">5.4. Validation method</a>

Each generated class defines the validation methods

~~~java
    boolean validate(FieldValidateHandler handler);
    boolean audit(FieldValidateHandler handler);
~~~

The *validate* method performs checks on all fields, the *audit* method only on the fields marked with the `audit`
attribute. If a field is redefined it is not considered in the validation checks.

The `FieldValidateHandler` interface is simply

~~~java
public interface FieldValidateHandler {
    void error(String name, int offset, int length, int column, ValidateError code);
}
~~~

When an error occurs, the `error` method is called with the name of the field in error, its offset, its length, the
position of the character in error and the type of error.

The possible types of errors are

~~~java
public enum ValidateError {
    NotNumber, NotAscii, NotLatin, NotValid, Mismatch
}
~~~

with the meaning
`NotNumber`
: non-numeric character in a numeric field

`NotAscii`
: non-ASCII character in an alphanumeric field where Ascii control is required

`NotLatin`
: non-Latin1 (ISO-8859-1) character in an alphanumeric field where the Latin1 control is required

`NotValid`
: character that fails the `Character.isDefined(c)` test in an alphanumeric field where the Valid check is required

`Mismatch`
: value other than expected in a constant field

<!--
## <a name="54">5.4. `boolean validate(FieldValidateHandler handler)`</a>
## <a name="55">5.5. `boolean audit(FieldValidateHandler handler)`</a>
-->
