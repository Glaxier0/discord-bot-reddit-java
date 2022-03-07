# postgresql-create-script #
### default user is postgres but if you have a user dont forget to give it permissions
### Before using this script create database named reddit_bot
### CREATE DATABASE reddit_bot;



-- Table: public.posts

-- DROP TABLE public.posts;

CREATE TABLE posts
(
id SERIAL PRIMARY KEY,
url character varying(250) COLLATE pg_catalog."default" NOT NULL,
author character varying(50) COLLATE pg_catalog."default",
title character varying(512) COLLATE pg_catalog."default",
created date,
subreddit character varying(50) COLLATE pg_catalog."default",
type character varying(50) COLLATE pg_catalog."default",
download_url character varying(512) COLLATE pg_catalog."default",
firebase_url character varying(255) COLLATE pg_catalog."default",
perma_url character varying(255) COLLATE pg_catalog."default",
CONSTRAINT posts_download_url_key UNIQUE (download_url),
CONSTRAINT posts_perma_url_key UNIQUE (perma_url),
CONSTRAINT posts_url_key UNIQUE (url),
CONSTRAINT posts_firebase_url_key UNIQUE (firebase_url)
)

TABLESPACE pg_default;

ALTER TABLE public.posts
OWNER to postgres;

GRANT ALL ON TABLE public.posts TO postgres;
GRANT USAGE, SELECT ON SEQUENCE posts_id_seq TO postgres;

-- Table: public.todo

-- DROP TABLE public.todo;

CREATE TABLE public.todo
(
id SERIAL PRIMARY KEY,
discord_user character varying(56) COLLATE pg_catalog."default" NOT NULL,
todo_row character varying(1024) COLLATE pg_catalog."default" NOT NULL,
created date NOT NULL,
is_completed boolean,
user_with_tag character varying(56) COLLATE pg_catalog."default"
)

TABLESPACE pg_default;

ALTER TABLE public.todo
OWNER to postgres;

GRANT ALL ON TABLE public.todo TO postgres;
GRANT USAGE, SELECT ON SEQUENCE todo_id_seq TO postgres;


CREATE TABLE users
(
id SERIAL PRIMARY KEY,
user_id VARCHAR(256) UNIQUE,
text_count INTEGER,
h_count INTEGER,
p_count INTEGER,
reddit_count INTEGER,
todo_count INTEGER,
user_with_tag VARCHAR(56)
)


ALTER TABLE users OWNER to postgres;
GRANT ALL ON TABLE users TO postgres;
GRANT USAGE, SELECT ON SEQUENCE users_id_seq TO postgres;

CREATE TABLE public.guilds
(
    id integer NOT NULL DEFAULT nextval('guilds_id_seq'::regclass),
    guild_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    guild_name character varying(64) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT guilds_pkey PRIMARY KEY (id),
    CONSTRAINT guilds_guild_id_key UNIQUE (guild_id),
    CONSTRAINT guilds_guild_name_key UNIQUE (guild_name)
)

TABLESPACE pg_default;

ALTER TABLE public.guilds
    OWNER to postgres;

GRANT ALL ON TABLE public.guilds TO postgres;
