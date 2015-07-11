# Lyricist

A blog engine for Pippo.


## How to use

Configure the data directories in the Pippo application.properties file.
```
# Lyricist
lyricist.blogs = rootBlog:root, blog:blog
```
Use the key "lyricist.blogs" for a list of data sources or sub-blogs.
Each key inside the list consists of a pair of [blog name]:[blog data directory]. In this example there is a blog called
"rootBlog" and it uses the data directory "root", which translates to "resources/lyricist/root/".

In each data directory there are all posts located. The posts are Markdown formatted with a YAML header.

```
---
layout: post
title: First Post
slug: first-post
tags: [blog, fun]
category: [java, web]
published: 2015-07-01T01:02:03
valid_until: 2015-08-01T00:00:00
draft: false
authors: [admin, rygel]
context: 
  pageWide
---
This is the first posting using Lyricist.
```
Between the two ```---``` there is the embedded YAML header. The rest of the file is Markdown formatted. The Pegdown 
library is used to handle the conversion from Markdown to HTML.


## TODO
- Add the ability to change context manually for each blog, e.g. lyricist.changeContext();
- Add the ability to add context to each document.
- Automatic slugification of file name if no slug is given!
- Use a directory watcher to look for new or removed posts and update the blog accordingly.
- Add a read more link.
- Let the read more link be configurable.
- Add separate directory for author markdown files.
- Add and use authors field in front matter.
- Add route for displaying all authors of a site.
- Add routes for categories, tags, archive.
- Get a list of all tags, with the no of how often they are used (to create tag clouds).

## DONE
- Add global static context for each blog.
