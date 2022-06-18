select p.name, c.name from person as p left join company as c on p.company_id = c.id where p.company_id != 5;


select c.name, count(c.id) from company as c right join person as p on c.id = p.company_id
group by (c.name)
having count(c.id) =
(select MAX(count_id) from (select cc.name, count(cc.id) as count_id from company as cc right join person as pp on cc.id = pp.company_id group by(cc.name)) as count_id);

