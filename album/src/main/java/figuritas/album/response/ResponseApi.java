package figuritas.album.response;

public record ResponseApi<T>(
        String status,
        String mensaje,
        T data
) {
    public static <T> ResponseApi<T> success(String mensaje, T data) {
        return new ResponseApi<>("success", mensaje, data);
    }

    public static <T> ResponseApi<T> error(String mensaje, T data) {
        return new ResponseApi<>("error", mensaje, data);
    }
}
