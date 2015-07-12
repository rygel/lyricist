# Lyricist

A blog engine for [Pippo](https://github.com/decebals/pippo). It is inspired by [Poet](http://jsantell.github.io/poet/). It supports the following features:
- drafts
- authors
- categories
- tags
- blog and post level context
- multiple blogs
- valid until management


## How to use

Configure the data directories in the Pippo application.properties file.
```
# Lyricist
lyricist.blogs = rootBlog:root, blog:blog
```
Use the key "lyricist.blogs" for a list of data sources or sub-blogs.
Each key inside the list consists of a pair of [blog name]:[blog main directory]. In this example there is a blog called
"rootBlog" and it uses the main directory "root", which translates to "resources/lyricist/root/".

In each data directory there are two subdirectories: ```authors``` and ```posts```. In ```posts``` there are all posts 
located. In ```authors``` are files for all authors of the blog located. Both the posts and authors are Markdown 
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
Between the two ```---``` there is the embedded YAML header. The rest of the file is Markdown formatted. The Pegdown 
library is used to handle the conversion from Markdown to HTML.


#### Embed Lyricist in Pippo
In your PippoApplication (where all routes are defined) 
```java
Map<String, Object> blogContext = new TreeMap<>();
blogContext.put("pageTitle", "Lyricist Blog");
Lyricist lyricist = new Lyricist(this);
lyricist.registerBlog("rootBlog", "/");
lyricist.registerBlog("blog", "/blog/", blogContext);
```

### Default Front Matter keys for Posts:
Key           | Description
------------- | -------------
layout        | The name of the layout file as registered with Pippo.
title         | The title of the post.
slug          | The slug of the post (optional).
tags          | The tags of the post (optional). **(not yet implemented)**
category      | The categories this post belongs to (optional). **(not yet implemented)**
draft         | Flag (true, false) if the post is in draft state or not. **(not yet implemented)**
published     | Date when the post is/was published.
validUntil    | Date until when the post will be valid. **(not yet implemented)**
authors       | List of authors for this post. See author support.
context       | Additional post level context.

## Concepts

### Context
To be able to render every blog posting, author page, etc., a so called context is required. This context provides the 
data needed by the template engine to render the page. For example the ```title``` of a blog post is part of the context.
In the layout file for the post the template engine accesses the context to get the post title. There are different 
context levels inside Lyricist. 
#### Blog Level Context
The blog level context is part of the whole blog, i.e. it will be accessible inside every post. It can be defined via
the overloaded ```registerBlog(name, pattern, context)``` method. The context object is a ```Map<String, Object>```.
```java
Map<String, Object> blogContext = new TreeMap<>();
blogContext.put("pageTitle", "Lyricist Blog");
lyricist.registerBlog("blog", "/blog/", blogContext);
```

#### Post Level Context
The post level context is part of the specific post, i.e. it will be only accessible inside that specific post.

## TODO
- Add the ability to change context manually for each blog, e.g. lyricist.changeContext();
- Add the ability to add context to each document.
- Automatic slugification of file name if no slug is given!
- Use a directory watcher to look for new or removed posts and update the blog accordingly.
- Add a read more link.
- Let the read more link be configurable.
- Add route for displaying all authors of a site.
- Add routes for categories, tags, archive.
- Add validUntil support.
- Add draft support.

## DONE
- Add global static context for each blog.
- Add separate directory for author markdown files.
- Add and use authors field in front matter.


## Dependencies
- SnakeYaml
- Pegdown
- Apache Commons IO
