-- Listado de Empleados
SELECT * FROM recursos_humano_db.empleado;

-- Visualizar los detalles del departamento y localización para un empleado específico.
SELECT E.documento_identidad, E.primer_nombre, E.primer_apellido, D.nombre AS 'departamento', L.direccion, Ci.nombre AS 'ciudad', Pa.nombre AS 'pais'
FROM recursos_humano_db.empleado AS E
INNER JOIN recursos_humano_db.departamento AS D ON E.departamento_id = D.id
INNER JOIN recursos_humano_db.localizacion AS L ON D.localizacion_id = L.id
INNER JOIN recursos_humano_db.ciudad AS Ci ON L.ciudad_id = Ci.id
INNER JOIN recursos_humano_db.pais AS Pa ON Ci.pais_id = Pa.id
WHERE E.id = 1;

-- Visualizar los empleados que están por encima del sueldo medio de su cargo.
SELECT E.primer_nombre, E.primer_apellido, E.sueldo, C.nombre AS 'cargo', C.sueldo_minimo, C.sueldo_maximo
FROM recursos_humano_db.empleado AS E
INNER JOIN recursos_humano_db.cargo AS C ON E.cargo_id = C.id
WHERE E.sueldo > (C.sueldo_minimo + C.sueldo_maximo) / 2;

-- Visualizar el historial laboral de un empleado.
SELECT E.primer_nombre, E.primer_apellido, C.nombre AS 'cargo', D.nombre AS 'departamento', H.fecha_retiro
FROM recursos_humano_db.historico AS H
INNER JOIN recursos_humano_db.empleado AS E ON H.empleado_id = E.id
INNER JOIN recursos_humano_db.cargo AS C ON H.cargo_id = C.id
INNER JOIN recursos_humano_db.departamento AS D ON H.departamento_id = D.id;

-- Visualizar los empleados que no tienen un gerente asignado.
SELECT * FROM recursos_humano_db.empleado WHERE gerente_id IS NULL;