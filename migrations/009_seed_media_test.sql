-- Datos de prueba para la tabla media.
-- Usa el usuario administrador existente. Si no existe id_usuario = 2,
-- cambia ese valor por un usuario valido de tu tabla usuarios.

DELETE FROM media
WHERE titulo IN (
    'Tecnica de pase',
    'Control de balon',
    'Finalizacion',
    'Rutina de calentamiento',
    'Estiramientos',
    'Material de entrenamiento'
);

INSERT INTO media (
    id_usuario,
    titulo,
    descripcion,
    url_archivo,
    mime_type,
    url_miniatura
) VALUES
(
    2,
    'Tecnica de pase',
    'Video de prueba para comprobar listado y detalle de media.',
    'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
    'video/mp4',
    'http://10.0.2.2:8000/static/media-test/thumb_tecnica_pase.png'
),
(
    2,
    'Control de balon',
    'Segundo video de prueba para validar varias filas en Android.',
    'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
    'video/mp4',
    'http://10.0.2.2:8000/static/media-test/thumb_control_balon.png'
),
(
    2,
    'Finalizacion',
    'Tercer video de prueba para validar detalle y reproductor.',
    'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4',
    'video/mp4',
    'http://10.0.2.2:8000/static/media-test/thumb_finalizacion.png'
),
(
    2,
    'Rutina de calentamiento',
    'Imagen de prueba para validar contenido no-video en media.',
    'http://10.0.2.2:8000/static/media-test/img_calentamiento.png',
    'image/png',
    NULL
),
(
    2,
    'Estiramientos',
    'Imagen de prueba para validar miniaturas y listado admin.',
    'http://10.0.2.2:8000/static/media-test/img_estiramientos.png',
    'image/png',
    NULL
),
(
    2,
    'Material de entrenamiento',
    'Imagen de prueba para validar registros de tipo imagen.',
    'http://10.0.2.2:8000/static/media-test/img_material.png',
    'image/png',
    NULL
);
