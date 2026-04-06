package fr.istic.taa.jaxrs.dto;

// Pour que tous les retours api soit de la meme forme

public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "OK", data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "Cree", data);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message, null);
    }

    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(204, "Delete", null);
    }

    // ─── Get ────────────────────────────────────────────────────────────

    public int getStatus()      { return status; }
    public String getMessage()  { return message; }
    public T getData()          { return data; }

	public static Object error(String string) {
		// TODO Auto-generated method stub
		return string;
	}
}