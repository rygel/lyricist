# Lyricist

A blog engine for Pippo. It is inspired by [Poet](http://jsantell.github.io/poet/).


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
tags          | The tags of the post (optional).
category      | The categories this post belongs to (optional).

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
