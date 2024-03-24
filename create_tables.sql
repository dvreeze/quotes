
create table quote (
    id int not null auto_increment,
    text varchar(5000) not null,
    attributedTo varchar(255) not null,
    primary key (id)
);

create table quote_subject (
    quote_id int not null,
    subject varchar(255) not null,
    primary key (quote_id, subject),
    foreign key (quote_id) references quote(id)
);
