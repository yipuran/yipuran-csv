# CSV読込み、書込み処理

RFC4180準拠、ラムダ式、ストリーム実行の為のＣＳＶ読込み、書込みのライブラリ

## Document
 [Wiki Page](../../wiki)

## Setup pom.xml
```
<repositories>
   <repository>
      <id>yipuran-core</id>
      <url>https://raw.github.com/yipuran/yipuran-csv/mvn-repo</url>
   </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.yipuran.csv</groupId>
        <artifactId>yipuran-csv</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>

```

## Setup gradle
```
repositories {
    mavebCentral()
    maven { url 'https://github.com/yipuran/yipuran-csv/mvn-repo'  }
}

dependencied {
    compile 'org.yipuran.csv:yipuran-csv:1.0'
}
```
