package com.example.labball_tfg.Modelo

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

// Define el modelo o contrato modelo api dao usado para comunicar la app con la API.
interface ModeloApiDao {

    // AUTH

    // Crea register mediante la API y comunica el resultado.
    @POST("/auth/register")
    suspend fun register(
        @Header("Authorization") token: String,
        @Body request: UsuarioCreateRequest
    ): Response<AuthUserResponse>

    // Gestiona login y devuelve el resultado de autenticacion.
    @POST("/auth/login")
    suspend fun login(
        @Header("Authorization") token: String
    ): Response<AuthUserResponse>

    // USUARIO AUTENTICADO

    // Carga get usuario me desde la API y actualiza el estado asociado.
    @GET("/usuarios/me")
    suspend fun getUsuarioMe(
        @Header("Authorization") token: String
    ): Response<UsuarioMeResponse>

    // Actualiza update telefono me y refleja la respuesta en la pantalla.
    @PUT("/usuarios/me/telefono")
    suspend fun updateTelefonoMe(
        @Header("Authorization") token: String,
        @Body request: UsuarioTelefonoUpdateRequest
    ): Response<UsuarioMeResponse>

    // Actualiza update perfil me y refleja la respuesta en la pantalla.
    @PUT("/usuarios/me/perfil")
    suspend fun updatePerfilMe(
        @Header("Authorization") token: String,
        @Body request: UsuarioPerfilUpdateRequest
    ): Response<UsuarioMeResponse>

    // Actualiza update foto perfil me y refleja la respuesta en la pantalla.
    @PUT("/usuarios/me/foto-perfil")
    suspend fun updateFotoPerfilMe(
        @Header("Authorization") token: String,
        @Body request: UsuarioFotoPerfilUpdateRequest
    ): Response<UsuarioMeResponse>

    // Encapsula la operacion upload foto perfil usada por la pantalla o el estado.
    @POST("/usuarios/me/foto-perfil/upload")
    suspend fun uploadFotoPerfil(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("X-File-Name") fileName: String,
        @Body body: okhttp3.RequestBody
    ): Response<UsuarioMeResponse>

    // TARIFAS

    // Carga get tarifas desde la API y actualiza el estado asociado.
    @GET("/tarifas/")
    suspend fun getTarifas(
        @Header("Authorization") token: String
    ): Response<TarifaListResponse>


    // RESERVAS

    // Crea create reserva mediante la API y comunica el resultado.
    @POST("/reservas/")
    suspend fun createReserva(
        @Header("Authorization") token: String,
        @Body request: ReservaCreateRequest
    ): Response<ReservaMessageResponse>

    // Encapsula la operacion preview reserva usada por la pantalla o el estado.
    @POST("/reservas/preview")
    suspend fun previewReserva(
        @Header("Authorization") token: String,
        @Body request: ReservaPreviewRequest
    ): Response<ReservaPreviewResponse>

    // Carga get mis reservas desde la API y actualiza el estado asociado.
    @GET("/reservas/me")
    suspend fun getMisReservas(
        @Header("Authorization") token: String
    ): Response<ReservaListResponse>

    // Carga get mi reserva by id desde la API y actualiza el estado asociado.
    @GET("/reservas/me/{id_reserva}")
    suspend fun getMiReservaById(
        @Header("Authorization") token: String,
        @Path("id_reserva") idReserva: Int
    ): Response<ReservaResponse>


    // ENTRENAMIENTOS DEL USUARIO

    // Carga get mis entrenamientos desde la API y actualiza el estado asociado.
    @GET("/entrenamientos/me")
    suspend fun getMisEntrenamientos(
        @Header("Authorization") token: String
    ): Response<EntrenamientoListResponse>

    // Carga get mi entrenamiento by id desde la API y actualiza el estado asociado.
    @GET("/entrenamientos/me/{id_entrenamiento}")
    suspend fun getMiEntrenamientoById(
        @Header("Authorization") token: String,
        @Path("id_entrenamiento") idEntrenamiento: Int
    ): Response<EntrenamientoResponse>


    // VIDEOS

    // Carga get videos desde la API y actualiza el estado asociado.
    @GET("/media/videos")
    suspend fun getVideos(
        @Header("Authorization") token: String
    ): Response<VideoListResponse>

    // Carga get video by id desde la API y actualiza el estado asociado.
    @GET("/media/videos/{id_media}")
    suspend fun getVideoById(
        @Header("Authorization") token: String,
        @Path("id_media") idMedia: Int
    ): Response<VideoDetailResponse>


    // JUGADOR DEL USUARIO

    // Carga get jugador me desde la API y actualiza el estado asociado.
    @GET("/jugadores/me")
    suspend fun getJugadorMe(
        @Header("Authorization") token: String
    ): Response<JugadorResponse>


    // ADMIN - CATALOGOS SUPERADMIN


    // Carga get admin entrenadores desde la API y actualiza el estado asociado.
    @GET("/admin/entrenadores")
    suspend fun getAdminEntrenadores(
        @Header("Authorization") token: String
    ): Response<EntrenadorListResponse>

    // Carga get admin ubicaciones desde la API y actualiza el estado asociado.
    @GET("/admin/ubicaciones")
    suspend fun getAdminUbicaciones(
        @Header("Authorization") token: String
    ): Response<UbicacionListResponse>

    // Crea create admin ubicacion mediante la API y comunica el resultado.
    @POST("/admin/ubicaciones")
    suspend fun createAdminUbicacion(
        @Header("Authorization") token: String,
        @Body request: UbicacionCreateRequest
    ): Response<UbicacionMessageResponse>

    // Elimina delete admin ubicacion y sincroniza el estado local.
    @DELETE("/admin/ubicaciones/{id_ubicacion}")
    suspend fun deleteAdminUbicacion(
        @Header("Authorization") token: String,
        @Path("id_ubicacion") idUbicacion: Int
    ): Response<AdminDeleteMessageResponse>

    // Carga get admin descuentos desde la API y actualiza el estado asociado.
    @GET("/admin/descuentos")
    suspend fun getAdminDescuentos(
        @Header("Authorization") token: String
    ): Response<DescuentoListResponse>

    // Crea create admin descuento mediante la API y comunica el resultado.
    @POST("/admin/descuentos")
    suspend fun createAdminDescuento(
        @Header("Authorization") token: String,
        @Body request: DescuentoAdminCreateRequest
    ): Response<DescuentoMessageResponse>

    // Actualiza update admin descuento y refleja la respuesta en la pantalla.
    @PUT("/admin/descuentos/{id_descuento}")
    suspend fun updateAdminDescuento(
        @Header("Authorization") token: String,
        @Path("id_descuento") idDescuento: Int,
        @Body request: DescuentoUpdateRequest
    ): Response<DescuentoMessageResponse>

    // Elimina delete admin descuento y sincroniza el estado local.
    @DELETE("/admin/descuentos/{id_descuento}")
    suspend fun deleteAdminDescuento(
        @Header("Authorization") token: String,
        @Path("id_descuento") idDescuento: Int
    ): Response<AdminDeleteMessageResponse>

    // ADMIN - USUARIOS

    // Carga get admin usuarios desde la API y actualiza el estado asociado.
    @GET("/admin/usuarios")
    suspend fun getAdminUsuarios(
        @Header("Authorization") token: String
    ): Response<AdminUsuarioListResponse>

    // Crea create admin usuario mediante la API y comunica el resultado.
    @POST("/admin/usuarios")
    suspend fun createAdminUsuario(
        @Header("Authorization") token: String,
        @Body request: AdminUsuarioCreateRequest
    ): Response<AdminUsuarioMessageResponse>

    // Actualiza update admin usuario rol y refleja la respuesta en la pantalla.
    @PUT("/admin/usuarios/{id_usuario}/rol")
    suspend fun updateAdminUsuarioRol(
        @Header("Authorization") token: String,
        @Path("id_usuario") idUsuario: Int,
        @Body request: AdminUsuarioRolUpdateRequest
    ): Response<AdminUsuarioMessageResponse>

    // Elimina delete admin usuario y sincroniza el estado local.
    @DELETE("/admin/usuarios/{id_usuario}")
    suspend fun deleteAdminUsuario(
        @Header("Authorization") token: String,
        @Path("id_usuario") idUsuario: Int
    ): Response<AdminUsuarioDeleteResponse>


    // ADMIN - RESERVAS

    // Carga get admin reservas desde la API y actualiza el estado asociado.
    @GET("/admin/reservas")
    suspend fun getAdminReservas(
        @Header("Authorization") token: String
    ): Response<ReservaAdminListResponse>

    // Carga get admin reserva by id desde la API y actualiza el estado asociado.
    @GET("/admin/reservas/{id_reserva}")
    suspend fun getAdminReservaById(
        @Header("Authorization") token: String,
        @Path("id_reserva") idReserva: Int
    ): Response<ReservaResponse>

    // Actualiza update reserva pagado y refleja la respuesta en la pantalla.
    @PUT("/admin/reservas/{id_reserva}/pagado")
    suspend fun updateReservaPagado(
        @Header("Authorization") token: String,
        @Path("id_reserva") idReserva: Int,
        @Body request: ReservaPagadoUpdateRequest
    ): Response<ReservaMessageResponse>

    // Encapsula la operacion asignar entrenamientos reserva usada por la pantalla o el estado.
    @POST("/admin/reservas/{id_reserva}/entrenamientos/asignar")
    suspend fun asignarEntrenamientosReserva(
        @Header("Authorization") token: String,
        @Path("id_reserva") idReserva: Int,
        @Body request: ReservaEntrenamientosAsignarRequest
    ): Response<ReservaEntrenamientosAsignarResponse>

    // Carga get entrenamientos reserva admin desde la API y actualiza el estado asociado.
    @GET("/admin/reservas/{id_reserva}/entrenamientos")
    suspend fun getEntrenamientosReservaAdmin(
        @Header("Authorization") token: String,
        @Path("id_reserva") idReserva: Int
    ): Response<EntrenamientoListResponse>

    // Actualiza update entrenamientos reserva admin y refleja la respuesta en la pantalla.
    @PUT("/admin/reservas/{id_reserva}/entrenamientos")
    suspend fun updateEntrenamientosReservaAdmin(
        @Header("Authorization") token: String,
        @Path("id_reserva") idReserva: Int,
        @Body request: ReservaEntrenamientosAsignarRequest
    ): Response<ReservaEntrenamientosAsignarResponse>


    // ADMIN - VIDEOS



    // Encapsula la operacion upload video usada por la pantalla o el estado.
    @Multipart
    @POST("/admin/media/videos/upload")
    suspend fun uploadVideo(
        @Header("Authorization") token: String,
        @Part("titulo") titulo: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part videoFile: MultipartBody.Part
    ): Response<MediaMessageResponse>

    // Actualiza update video y refleja la respuesta en la pantalla.
    @PUT("/admin/media/videos/{id_media}")
    suspend fun updateVideo(
        @Header("Authorization") token: String,
        @Path("id_media") idMedia: Int,
        @Body request: MediaUpdateRequest
    ): Response<MediaMessageResponse>

    // Elimina delete video y sincroniza el estado local.
    @DELETE("/admin/media/videos/{id_media}")
    suspend fun deleteVideo(
        @Header("Authorization") token: String,
        @Path("id_media") idMedia: Int
    ): Response<MediaMessageResponse>


    // ADMIN - JUGADORES

    // Carga get admin jugadores desde la API y actualiza el estado asociado.
    @GET("/admin/jugadores")
    suspend fun getAdminJugadores(
        @Header("Authorization") token: String
    ): Response<JugadorListResponse>

    // Carga get admin jugador by id desde la API y actualiza el estado asociado.
    @GET("/admin/jugadores/{id_jugador}")
    suspend fun getAdminJugadorById(
        @Header("Authorization") token: String,
        @Path("id_jugador") idJugador: Int
    ): Response<JugadorResponse>

    // Crea create jugador mediante la API y comunica el resultado.
    @POST("/admin/jugadores")
    suspend fun createJugador(
        @Header("Authorization") token: String,
        @Body request: JugadorCreateRequest
    ): Response<JugadorMessageResponse>

    // Actualiza update jugador y refleja la respuesta en la pantalla.
    @PUT("/admin/jugadores/{id_jugador}")
    suspend fun updateJugador(
        @Header("Authorization") token: String,
        @Path("id_jugador") idJugador: Int,
        @Body request: JugadorUpdateRequest
    ): Response<JugadorMessageResponse>

    // Actualiza update jugador estadisticas y refleja la respuesta en la pantalla.
    @PUT("/admin/jugadores/{id_jugador}/estadisticas")
    suspend fun updateJugadorEstadisticas(
        @Header("Authorization") token: String,
        @Path("id_jugador") idJugador: Int,
        @Body request: JugadorEstadisticasUpdateRequest
    ): Response<JugadorMessageResponse>
}


