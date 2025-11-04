package figuritas.album.response;

public record MessageResponse(String status, String mensaje) {
    public static MessageResponse success(String mensaje) {
        return new MessageResponse("success", mensaje);
    }
}
