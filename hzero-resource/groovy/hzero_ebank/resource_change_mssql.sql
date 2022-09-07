ALTER TABLE hebk_event_referd_resource ADD event_resource VARCHAR ( 240 );

EXECUTE sp_addextendedproperty
N'MS_Description', '资源', N'user', N'dbo', N'table', N'hebk_event_referd_resource', N'column', N'event_resource';

UPDATE herr SET herr.event_resource = herr1.resource FROM hebk_event_referd_resource herr, hebk_event_referd_resource herr1 where herr1.resource_id = herr.resource_id;

ALTER TABLE hebk_event_referd_resource DROP COLUMN resource;