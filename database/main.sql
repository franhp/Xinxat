-- phpMyAdmin SQL Dump
-- version 3.3.9
-- http://www.phpmyadmin.net
--
-- Servidor: localhost
-- Temps de generaciŒ·ŒıŒÂ: 01-10-2011 a les 19:45:21
-- VersiŒ·ŒıŒÂ del servidor: 5.5.8
-- VersiŒ·ŒıŒÂ de PHP : 5.3.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Base de dades: `framework`
--

-- --------------------------------------------------------

--
-- Estructura de la taula `categories`
--

CREATE TABLE IF NOT EXISTS `categories` (
 `id` int(11) NOT NULL DEFAULT '0',
  `category_name` varchar(50) DEFAULT NULL,
   PRIMARY KEY (`id`)
   ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

   -- --------------------------------------------------------

   --
   -- Estructura de la taula `comments`
   --

   CREATE TABLE IF NOT EXISTS `comments` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
     `userid` int(11) NOT NULL,
      `postid` int(11) NOT NULL,
       `comment` varchar(1000) NOT NULL,
        `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	 PRIMARY KEY (`id`)
	 ) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=21 ;

	 -- --------------------------------------------------------

	 --
	 -- Estructura de la taula `posts`
	 --

	 CREATE TABLE IF NOT EXISTS `posts` (
	  `id` int(11) NOT NULL AUTO_INCREMENT,
	   `title` varchar(100) NOT NULL,
	    `body` varchar(5000) NOT NULL,
	     `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	      `userid` int(11) NOT NULL,
	       `seourl` varchar(50) NOT NULL,
	        PRIMARY KEY (`id`),
		 KEY `orderByDate` (`date`)
		 ) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=26 ;

		 -- --------------------------------------------------------

		 --
		 -- Estructura de la taula `posts_categories`
		 --

		 CREATE TABLE IF NOT EXISTS `posts_categories` (
		  `id` int(11) NOT NULL DEFAULT '0',
		   `id_post` int(11) DEFAULT NULL,
		    `id_category` int(11) DEFAULT NULL,
		     PRIMARY KEY (`id`)
		     ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

		     -- --------------------------------------------------------
		     --
		     -- Estructura de la taula `users`
		     --

		     CREATE TABLE IF NOT EXISTS `users` (
		      `id` int(11) NOT NULL AUTO_INCREMENT,
		       `username` varchar(25) NOT NULL,
		        `password` varchar(50) NOT NULL,
			 `name` varchar(100) NOT NULL,
			  `lastname` varchar(100) NOT NULL,
			  `lastlogin` varchar(100) NOT NULL,
			  `lastonline` varchar(100) NOT NULL,
			   `IP` varchar(10) NOT NULL,
			    `location` varchar(50) NOT NULL,
			     `birthdate` date NOT NULL,
			      `email` varchar(50) NOT NULL,
			       `role` int(11) NOT NULL DEFAULT '1',
			       `oauth_token` varchar(50),
			        PRIMARY KEY (`id`)
				) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=18 ;

