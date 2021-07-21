# postgresql-create-script #
### default user is postgres but if you have a user dont forget to give it permissions


-- Table: public.posts

-- DROP TABLE public.posts;

CREATE TABLE public.posts
(
    id integer NOT NULL DEFAULT nextval('posts_id_seq'::regclass),
    url character varying(250) COLLATE pg_catalog."default" NOT NULL,
    author character varying(50) COLLATE pg_catalog."default",
    title character varying(512) COLLATE pg_catalog."default",
    created date,
    subreddit character varying(50) COLLATE pg_catalog."default",
    type character varying(50) COLLATE pg_catalog."default",
    download_url character varying(512) COLLATE pg_catalog."default",
    vimeo_url character varying(255) COLLATE pg_catalog."default",
    perma_url character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT posts_pkey PRIMARY KEY (id),
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
