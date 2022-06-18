create table vacancies(
id_vacancies serial primary key,
title varchar(255),
url varchar(255) unique,
created_vacancies timestamp,
description text
);