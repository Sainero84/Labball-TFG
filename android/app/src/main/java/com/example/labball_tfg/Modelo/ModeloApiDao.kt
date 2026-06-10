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

interface ModeloApiDao {

    // AUTH

    @POST("/auth/register")
    suspend fun register(
        @Header("Authorization") token: String,
        @Body request: UsuarioCreateRequest
    ): Response<AuthUserResponse>

    @POST("/auth/login")
    suspend fun login(
        @Header("Authorization") token: String
    ): Response<AuthUserResponse>

    @POST("/auth/validate-token")
    suspend fun validateToken(
        @Header("Authorization") token: String
    ): Response<AuthUserResponse>

    @POST("/auth/recover-password")
    suspend fun recoverPassword(): Response<AuthMessageResponse>


    // USUARIO AUTENTICADO

    @GET("/usuarios/me")
    suspend fun getUsuarioMe(
        @Header("Authorization") token: String
    ): Response<UsuarioMeResponse>

    @PUT("/usuarios/me/telefono")
    suspend fun updateTelefonoMe(
        @Header("Authorization") token: String,
        @Body request: UsuarioTelefonoUpdateRequest
    ): Response<UsuarioMeResponse>

    @PUT("/usuarios/me/perfil")
    suspend fun updatePerfilMe(
        @Header("Authorization") token: String,
        @Body request: UsuarioPerfilUpdateRequest
    ): Response<UsuarioMeResponse>

    @PUT("/usuarios/me/foto-perfil")
    suspend fun updateFotoPerfilMe(
        @Header("Authorization") token: String,
        @Body request: UsuarioFotoPerfilUpdateRequest
    ): Response<UsuarioMeResponse>

    @POST("/usuarios/me/foto-perfil/upload")
    suspend fun uploadFotoPerfil(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Header("X-File-Name") fileName: String,
        @Body body: okhttp3.RequestBody
    ): Response<UsuarioMeResponse>

    // TARIFAS

    @GET("/tarifas/")
    suspend fun getTarifas(
        @Header("Authorization") token: String
    ): Response<TarifaListResponse>


    // RESERVAS

    @POST("/reservas/")
    suspend fun createReserva(
        @Header("Authorization") token: String,
        @Body request: ReservaCreateRequest
    ): Response<ReservaMessageResponse>

    @POST("/reservas/preview")
    suspend fun previewReserva(
        @Header("Authorization") token: String,
        @Body request: ReservaPreviewRequest
    ): Response<ReservaPreviewResponse>

    @GET("/reservas/me")
    suspend fun getMisReservas(
        @Header("Authorization") token: String
    ): Response<ReservaListResponse>

    @GET("/reservas/me/{id_reserva}")
    suspend fun getMiReservaById(
        @Header("Authorization") token: String,
        @Path("id_reserva") idReserva: Int
    ): Response<ReservaResponse>


    // ENTRENAMIENTOS DEL USUARIO

    @GET("/entrenamientos/me")
    suspend fun getMisEntrenamientos(
        @Header("Authorization") token: String
    ): Response<EntrenamientoListResponse>

    @GET("/entrenamientos/me/{id_entrenamiento}")
    suspend fun getMiEntrenamientoById(
        @Header("Authorization") token: String,
        @Path("id_entrenamiento") idEntrenamiento: Int
    ): Response<EntrenamientoResponse>


    // VIDEOS

    @GET("/media/videos")
    suspend fun getVideos(
        @Header("Authorization") token: String
    ): Response<VideoListResponse>

    @GET("/media/videos/{id_media}")
    suspend fun getVideoById(
        @Header("Authorization") token: String,
        @Path("id_media") idMedia: Int
    ): Response<VideoDetailResponse>


    // JUGADOR DEL USUARIO

    @GET("/jugadores/me")
    suspend fun getJugadorMe(
        @Header("Authorization") token: String
    ): Response<JugadorResponse>


    // ADMIN - CATALOGOS SUPERADMIN


    @GET("/admin/entrenadores")
    suspend fun getAdminEntrenadores(
        @Header("Authorization") token: String
    ): Response<EntrenadorListResponse>

    @GET("/admin/ubicaciones")
    suspend fun getAdminUbicaciones(
        @Header("Authorization") token: String
    ): Response<UbicacionListResponse>

    @POST("/admin/ubicaciones")
    suspend fun createAdminUbicacion(
        @Header("Authorization") token: String,
        @Body request: UbicacionCreateRequest
    ): Response<UbicacionMessageResponse>

    @DELETE("/admin/ubicaciones/{id_ubicacion}")
    suspend fun deleteAdminUbicacion(
        @Header("Authorization") token: String,
        @Path("id_ubicacion") idUbicacion: Int
    ): Response<AdminDeleteMessageResponse>

    @GET("/admin/descuentos")
    suspend fun getAdminDescuentos(
        @Header("Authorization") token: String
    ): Response<DescuentoListResponse>

    @POST("/admin/descuentos")
    suspend fun createAdminDescuento(
        @Header("Authorization") token: String,
        @Body request: DescuentoAdminCreateRequest
    ): Response<DescuentoMessageResponse>

    @PUT("/admin/descuentos/{id_descuento}")
    suspend fun updateAdminDescuento(
        @Header("Authorization") token: String,
        @Path("id_descuento") idDescuento: Int,
        @Body request: DescuentoUpdateRequest
    ): Response<DescuentoMessageResponse>

    @DELETE("/admin/descuentos/{id_descuento}")
    suspend fun deleteAdminDescuento(
        @Header("Authorization") token: String,
        @Path("id_descuento") idDescuento: Int
    ): Response<AdminDeleteMessageResponse>

    // ADMIN - USUARIOS

    @GET("/admin/usuarios")
    suspend fun getAdminUsuarios(
        @Header("Authorization") token: String
    ): Response<AdminUsuarioListResponse>

    @POST("/admin/usuarios")
    suspend fun createAdminUsuario(
        @Header("Authorization") token: String,
        @Body request: AdminUsuarioCreateRequest
    ): Response<AdminUsuarioMessageResponse>

    @PUT("/admin/usuarios/{id_usuario}/rol")
    suspend fun updateAdminUsuarioRol(
        @Header("Authorization") token: String,
        @Path("id_usuario") idUsuario: Int,
        @Body request: AdminUsuarioRolUpdateRequest
    ): Response<AdminUsuarioMessageResponse>

    @DELETE("/admin/usuarios/{id_usuario}")
    suspend fun deleteAdminUsuario(
        @Header("Authorization") token: String,
        @Path("id_usuario") idUsuario: Int
    ): Response<AdminUsuarioDeleteResponse>


    // ADMIN - TARIFAS

    @GET("/admin/tarifas")
    suspend fun getAdminTarifas(
        @Header("Authorization") token: String
    ): Response<TarifaListResponse>

    @GET("/admin/tarifas/{id_tarifa}")
    suspend fun getAdminTarifaById(
        @Header("Authorization") token: String,
        @Path("id_tarifa") idTarifa: Int
    ): Response<TarifaResponse>

    @POST("/admin/tarifas")
    suspend fun createTarifa(
        @Header("Authorization") token: String,
        @Body request: TarifaCreateRequest
    ): Response<TarifaMessageResponse>

    @PUT("/admin/tarifas/{id_tarifa}")
    suspend fun updateTarifa(
        @Header("Authorization") token: String,
        @Path("id_tarifa") idTarifa: Int,
        @Body request: TarifaUpdateRequest
    ): Response<TarifaMessageResponse>

    @DELETE("/admin/tarifas/{id_tarifa}")
    suspend fun deleteTarifa(
        @Header("Authorization") token: String,
        @Path("id_tarifa") idTarifa: Int
    ): Response<TarifaMessageResponse>


    // ADMIN - RESERVAS

    @GET("/admin/reservas")
    suspend fun getAdminReservas(
        @Header("Authorization") token: String
    ): Response<ReservaAdminListResponse>

    @GET("/admin/reservas/{id_reserva}")
    suspend fun getAdminReservaById(
        @Header("Authorization") token: String,
        @Path("id_reserva") idReserva: Int
    ): Response<ReservaResponse>

    @PUT("/admin/reservas/{id_reserva}/pagado")
    suspend fun updateReservaPagado(
        @Header("Authorization") token: String,
        @Path("id_reserva") idReserva: Int,
        @Body request: ReservaPagadoUpdateRequest
    ): Response<ReservaMessageResponse>

    @POST("/admin/reservas/{id_reserva}/entrenamientos/asignar")
    suspend fun asignarEntrenamientosReserva(
        @Header("Authorization") token: String,
        @Path("id_reserva") idReserva: Int,
        @Body request: ReservaEntrenamientosAsignarRequest
    ): Response<ReservaEntrenamientosAsignarResponse>

    @GET("/admin/reservas/{id_reserva}/entrenamientos")
    suspend fun getEntrenamientosReservaAdmin(
        @Header("Authorization") token: String,
        @Path("id_reserva") idReserva: Int
    ): Response<EntrenamientoListResponse>

    @PUT("/admin/reservas/{id_reserva}/entrenamientos")
    suspend fun updateEntrenamientosReservaAdmin(
        @Header("Authorization") token: String,
        @Path("id_reserva") idReserva: Int,
        @Body request: ReservaEntrenamientosAsignarRequest
    ): Response<ReservaEntrenamientosAsignarResponse>


    // ADMIN - ENTRENAMIENTOS

    @POST("/admin/entrenamientos")
    suspend fun createEntrenamiento(
        @Header("Authorization") token: String,
        @Body request: EntrenamientoCreateRequest
    ): Response<EntrenamientoMessageResponse>

    @PUT("/admin/entrenamientos/{id_entrenamiento}")
    suspend fun updateEntrenamiento(
        @Header("Authorization") token: String,
        @Path("id_entrenamiento") idEntrenamiento: Int,
        @Body request: EntrenamientoUpdateRequest
    ): Response<EntrenamientoMessageResponse>

    @DELETE("/admin/entrenamientos/{id_entrenamiento}")
    suspend fun deleteEntrenamiento(
        @Header("Authorization") token: String,
        @Path("id_entrenamiento") idEntrenamiento: Int
    ): Response<EntrenamientoMessageResponse>


    // ADMIN - VIDEOS



    @POST("/admin/media/videos")
    suspend fun createVideo(
        @Header("Authorization") token: String,
        @Body request: MediaCreateRequest
    ): Response<MediaMessageResponse>

    @Multipart
    @POST("/admin/media/videos/upload")
    suspend fun uploadVideo(
        @Header("Authorization") token: String,
        @Part("titulo") titulo: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part videoFile: MultipartBody.Part
    ): Response<MediaMessageResponse>

    @PUT("/admin/media/videos/{id_media}")
    suspend fun updateVideo(
        @Header("Authorization") token: String,
        @Path("id_media") idMedia: Int,
        @Body request: MediaUpdateRequest
    ): Response<MediaMessageResponse>

    @DELETE("/admin/media/videos/{id_media}")
    suspend fun deleteVideo(
        @Header("Authorization") token: String,
        @Path("id_media") idMedia: Int
    ): Response<MediaMessageResponse>


    // ADMIN - JUGADORES

    @GET("/admin/jugadores")
    suspend fun getAdminJugadores(
        @Header("Authorization") token: String
    ): Response<JugadorListResponse>

    @GET("/admin/jugadores/{id_jugador}")
    suspend fun getAdminJugadorById(
        @Header("Authorization") token: String,
        @Path("id_jugador") idJugador: Int
    ): Response<JugadorResponse>

    @POST("/admin/jugadores")
    suspend fun createJugador(
        @Header("Authorization") token: String,
        @Body request: JugadorCreateRequest
    ): Response<JugadorMessageResponse>

    @PUT("/admin/jugadores/{id_jugador}")
    suspend fun updateJugador(
        @Header("Authorization") token: String,
        @Path("id_jugador") idJugador: Int,
        @Body request: JugadorUpdateRequest
    ): Response<JugadorMessageResponse>

    @PUT("/admin/jugadores/{id_jugador}/estadisticas")
    suspend fun updateJugadorEstadisticas(
        @Header("Authorization") token: String,
        @Path("id_jugador") idJugador: Int,
        @Body request: JugadorEstadisticasUpdateRequest
    ): Response<JugadorMessageResponse>
}


