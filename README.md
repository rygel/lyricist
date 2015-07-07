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

In each data directory there are all posts located. The posts are Markdown formatted with a Yaml header.

```
---
layout: post
title: First Post
slug: first-post
published: 2015-07-01T01:02:03
valid_until: 2015-08-01T00:00:00
draft: false
---
This is the first posting using Lyricist.
```
