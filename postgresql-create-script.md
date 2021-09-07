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
    vimeo_url character varying(255) COLLATE pg_catalog."default",
    perma_url character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT posts_download_url_key UNIQUE (download_url),
    CONSTRAINT posts_perma_url_key UNIQUE (perma_url),
    CONSTRAINT posts_url_key UNIQUE (url),
    CONSTRAINT posts_vimeo_url_key UNIQUE (vimeo_url)
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
    todo_row character varying(256) COLLATE pg_catalog."default" NOT NULL,
    created date NOT NULL
)

TABLESPACE pg_default;

ALTER TABLE public.todo
    OWNER to postgres;

GRANT ALL ON TABLE public.todo TO postgres;
GRANT USAGE, SELECT ON SEQUENCE todo_id_seq TO postgres;
