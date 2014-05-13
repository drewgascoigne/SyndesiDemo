# --- !Ups

create table User (
	id int NOT NULL AUTO_INCREMENT,
    username varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null
   
  );


# --- !Downs

drop table User;