select p.name, c.name
from person as p
left join company as c
on p.company_id = c.id
where p.company_id != 5;


select c.name, count(c.id)
from company as c
right join person as p
on c.id = p.company_id
group by (c.name)
having count(c.id) =
(select count(company_id)
from person
group by company_id
order by count(company_id) desc
limit 1);
