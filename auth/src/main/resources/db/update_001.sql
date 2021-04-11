create table person (
id serial primary key not null,
login varchar(2000),
password varchar(2000)
);

insert into person (login, password) values ('parsentev', '123');
insert into person (login, password) values ('ban', '123');
insert into person (login, password) values ('ivan', '123');

create table employee (
id serial primary key not null,
first_name varchar(2000),
second_name varchar(2000),
inn varchar(20),
date date
);

create table employee_person (
employee_id int,
person_id int,
constraint fk_employee foreign key (employee_id) references employee(id),
constraint pr_key primary key (employee_id, person_id)
);

insert into employee (id, first_name, second_name, inn, date) values ('1', 'Petr', 'Arsentev', '1', '1999-01-08');
insert into employee_person (employee_id, person_id) values ('1', '1');
