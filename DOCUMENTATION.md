Lyricist Documentation
======================

[TOC levels=4]: # "## Table of contents"
## Table of contents
- [How to get](#how-to-get)
- [Dependencies](#dependencies)
- [Features](#features)
- [Quick Introduction](#quick-introduction)
- [Use Cases](#use-cases)
    - [Case 1: Only static pages on a website](#case-1-only-static-pages-on-a-website)
    - [Case 2: Only a blog](#case-2-only-a-blog)
    - [Case 3: Multiple static pages and multiple blogs on a website](#case-3-multiple-static-pages-and-multiple-blogs-on-a-website)
    - [Case 4: Configure authors of a blog](#case-4-configure-authors-of-a-blog)
    - [Case 5: Configure Categories of a blog](#case-5-configure-categories-of-a-blog)
    - [Case 6: Configure Tags of a blog](#case-6-configure-tags-of-a-blog)
- [Snippets](#snippets)
    - [Important directories](#important-directories)
    - [Global Blog context](#global-blog-context)
    - [Local Page context](#local-page-context)
    - [Global Blog layouts](#global-blog-layouts)
    - [Local Page layout](#local-page-layout)
    - [Automatic slug creation vs explicit slug naming](#automatic-slug-creation-vs-explicit-slug-naming)
    - [Data files location (resources vs external directoy)](#data-files-location-resources-vs-external-directoy)
    - [Multiple Blogs in one website](#multiple-blogs-in-one-website)
    - [Draft flag of posts (and static pages?)](#draft-flag-of-posts-and-static-pages)
- [Utility Functions](#utility-functions)
    - [EmailObfuscation](#emailobfuscation)
        - [Example](#example)


## How to get
Maven Central
```
<dependency>
    <groupId>io.andromeda</groupId>
    <artifactId>lyricist</artifactId>
    <version>0.2.0</version>
</dependency>
```

## Dependencies
- [SnakeYAML]((https://bitbucket.org/asomov/snakeyaml)) ([bundled by pippo]())
- [fastjson](https://github.com/alibaba/fastjson) ([bundled by pippo]())
- [SLF4J]()
- [flexmark-java](https://github.com/vsch/flexmark-java)
- [Apache Commons IO](https://commons.apache.org/proper/commons-io/)

## Features

## Quick Introduction

## Use Cases

### Case 1: Only static pages on a website

### Case 2: Only a blog

### Case 3: Multiple static pages and multiple blogs on a website

### Case 4: Configure authors of a blog

### Case 5: Configure Categories of a blog

### Case 6: Configure Tags of a blog


## Snippets

### Important directories

### Global Blog context

### Local Page context

### Global Blog layouts

### Local Page layout

### Automatic slug creation vs explicit slug naming

### Data files location (resources vs external directoy)

### Multiple Blogs in one website

### Draft flag of posts (and static pages?)

## Utility Functions
### EmailObfuscation
Use the static method ```String obfuscate(String email)``` of the ```Utilities``` class to obfuscate an email address.

#### Example
Code:
```
String email = Utilities.obfuscate("test@test.com");
context.put("email", email);
```
Template:
```
<a href="mailto:{{email | raw}}">{{email | raw}}</a>
```

Rendered output:
```
<a href="mailto:&#116;e&#115;&#x74;&#x40;&#x74;&#x65;&#115;&#x74;&#x2e;c&#111;&#x6d;">&#116;e&#115;&#x74;&#x40;&#x74;&#x65;&#115;&#x74;&#x2e;c&#111;&#x6d;</a>
```