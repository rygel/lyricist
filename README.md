Lyricist
=========
[![Maven Central](http://img.shields.io/maven-central/v/io.andromeda/lyricist.svg)](http://search.maven.org/#search|ga|1|io.andromeda)

A blog/static pages engine for the [Pippo](https://github.com/decebals/pippo) Java Micro Framework. It is inspired by
 [Poet](http://jsantell.github.io/poet/). It supports the following features:
- [Author Pages](#author_pages)
- [Static Pages](#static_pages)
- [Blog and Post Level Context](#context)
- Categories
- Drafts
- Multiple Blogs
- Tags
- JSON/YAML Front Matter
- Valid until management (not yet implemented)
- A Maven Quickstart Archetype (not yet implemented)

## Use Cases
Lyricist is useful for two main use cases:
1. As a blog (obviously).
2. As a semi-static site engine for websites. E.g. you can use Lyricist to hold/render your about, disclaimer, agb pages.

## How to use
Configure the data directories in the Pippo application.properties file.
```
# Lyricist
lyricist.blogs = rootBlog:root, blog:blog
```
Use the key "lyricist.blogs" for a list of blogs.
Each key inside the list consists of a pair of [blog name]:[blog main directory]. In this example there is a blog called
"rootBlog" and it uses the main directory "root", which translates to `resources/lyricist/root/`.

In each data directory there are two subdirectories: `authors` and `posts`. In `posts` there are all posts 
located. In `authors` are files for all authors of the blog located. Both the posts and authors are Markdown 
formatted with a YAML header. The authors directory is optional. If it doesn't exist or contains no files the author 
support is disabled for this blog.

```
---
layout: post
title: First Post
slug: first-post
tags: [blog, fun]
category: [java, web]
published: 2015-07-01T01:02:03
validUntil: 2015-08-01T00:00:00
draft: false
authors: [admin, rygel]
context: 
  pageWide
---
This is the first posting using Lyricist.
```
Between the two `---` there is the embedded YAML header. The rest of the file is Markdown formatted. The [Pegdown 
library](https://github.com/sirthias/pegdown) is used to handle the conversion from Markdown to HTML.


#### Embed Lyricist in Pippo
In your PippoApplication (where all routes are defined) 
Simple usage:
```java
Lyricist lyricist = new Lyricist(this);
lyricist.registerBlog("rootBlog", "/");
```

With Blog Level Context
```java
Map<String, Object> blogContext = new TreeMap<>();
blogContext.put("pageTitle", "Lyricist Blog");
Lyricist lyricist = new Lyricist(this);
lyricist.registerBlog("blog", "/blog/", blogContext);
```

Create your blog data directories in the directory `resources/lyricist`. For example:
```
resources
  |
  |-lyricist
     |
     |-root
     |  |
     |  |-posts
     |    |
     |    |-about.md
     |
     |-blog
       |
       |-authors
       |  |
       |  |-admin.md
       |  |-rygel.md
       |
       |-posts
          |
          |-post_01.md
          |-post_02.md
```

### Default Front Matter keys for Posts:
Key           | Description
------------- | -------------
layout        | The name of the layout file as registered with Pippo (mandatory).
title         | The title of the post.
slug          | The slug of the post (optional). When no slug is given, a default one is created. See [Slug Creation](#slug_creation).
tags          | The tags of the post (optional). **(not yet implemented)**
category      | The categories this post belongs to (optional). **(not yet implemented)**
draft         | Flag (true, false) if the post is in draft state or not.
published     | Date when the post is/was published.
validUntil    | Date until when the post will be valid. **(not yet implemented)**
authors       | List of authors for this post. Here the short name of an author must be used. See [Author Pages](#author_pages).
context       | Additional post level [context](#context).

## Concepts
Here are explanations of the core concepts of Lyricist.
### Author Pages<a name='author_pages'/>
There exists optional support for author pages (for biographies, etc.) in Lyricist. To enable this support you just have
to create a directory called `authors` below the blog main directory and next to the `posts` directory. Then for
every author inside this directory - and an author is a markdown file, very similar to a post - a author page is created.
The author pages have to route `[blog name]/authors/[author slug]`.

Example of an author Markdown file:
```
---
layout: author
shortName: rygel
firstName: Dominar Rygel
lastName: VII
context: 
  pageWide
---
The biography of the user Rygel.
```
#### Default Front Matter keys for Authors:
Key           | Description
------------- | -------------
layout        | The name of the layout file as registered with Pippo (mandatory).
shortName     | The short name of the author. This is used to identify the authors from a post (mandatory).
firstName     | The first name of the author (optional).
lastName      | The last name of the author (optional).
slug          | The slug of the author (optional). When no slug is given, a default one is created. See Slug Creation.
context       | Additional post level context.

### Context<a name='context'/>
To be able to render every blog posting, author page, etc., a so called context is required. This context provides the 
data needed by the template engine to render the page. For example the `title` of a blog post is part of the context.
In the layout file for the post the template engine accesses the context to get the post title. There are different 
context levels inside Lyricist. 
#### Blog Level Context
The blog level context is part of the whole blog, i.e. it will be accessible inside every post. It can be defined via
the overloaded `registerBlog(name, pattern, context)` method. The context object is a `Map<String, Object>`.
```java
Map<String, Object> blogContext = new TreeMap<>();
blogContext.put("pageTitle", "Lyricist Blog");
lyricist.registerBlog("blog", "/blog/", blogContext);
```
#### Post Level Context
The post level context is part of the specific post, i.e. it will be only accessible inside that specific post.

### Static Pages<a name='static_pages'/>
There exists support for static pages (for legal disclaimers, privacy policy, etc.) in Lyricist. The files are places in
a directory called `static_pages` below the blog main directory and next to the `posts` directory. Then for
every file inside this directory a static page is created.
The author pages have to route `[blog name]/[route]`. this means you have to either speciify the route in the front matter via
e.g. `route: privacy_policy` or if omitted, the filename without extension is used for the route.

Example of an static page Markdown file:
```
---
layout: static_pages
title: About
draft: false
slug: about
context: 
  pageWide
---
The biography of the user Rygel.
```
#### Default Front Matter keys for Static Pages:
Key           | Description
------------- | -------------
layout        | The name of the layout file as registered with Pippo (mandatory).
title         | The title of this page (optional).
draft         | If the page has draft status (not yet implemented).
slug          | The slug of the static page (optional). When no slug is given, a default one is created. See Slug Creation.
context       | Additional static page level context (optional).



### Multiple Blogs



### Slug Creation<a name='slug_creation'/>
TODO: Explain the default slug creation algorithm.


## Features
### Deployment
#### Blog/static data files location
Store the blog/static files data files either in the application jar or externally.
```
# Lyricist
lyricist.blogs = rootBlog:static-files, blog:data/blog
```
The first lblog definition is using the files from the application jar (TODO: verify!) and the second blog definition is
using a path external to the application jar, but relative to the application root directory to locate the blog files.
This is useful for bundling the static files with the application, but enables the addition of new blog posts without 
re-building and re-deploying the application jar.

## TODO
- Add the ability to change context manually for each blog, e.g. lyricist.changeContext();
- Add the ability to add context to each document.
- Use a directory watcher to look for new or removed posts and update the blog accordingly.
- Add a read more link.
- Let the read more link be configurable.
- Add route for displaying all authors of a site.
- Add routes for categories, tags, archive.
- Add validUntil support. Use quartz for this!
- Add checking if a route for a post already exists.
- Replace Pegdown by [flexmark](https://github.com/vsch/flexmark-java)
- Add support for hardcoded blog paths!
- Add support for JSON frontmatter.
- Add draft flag for static pages.
- Add multi-language support, i.e. multiple versions of the same blog post in different languages. Add new directory 
hierarchy under the main one. `blog/en/` means all posts in English, `blog/de/` all posts in german, with fallback to the default language.
- Create quickstart archetype for easier creation of projects.

## DONE
- Add global static context for each blog.
- Add separate directory for author markdown files.
- Add and use authors field in front matter.
- Add draft support.
- Automatic slug creation of file name if no slug is given!


## Dependencies
- [SnakeYaml](https://bitbucket.org/asomov/snakeyaml)
- [fastjson](https://github.com/alibaba/fastjson)
- [Pegdown](https://github.com/sirthias/pegdown)
- [Apache Commons IO](https://commons.apache.org/proper/commons-io/)
