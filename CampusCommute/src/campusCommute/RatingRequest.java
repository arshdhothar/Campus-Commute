package campusCommute;

public class RatingRequest {

    // The ID of the driver being rated
    private Long ratedPersonId;

    // The score given (1.0 to 5.0)
    private double rating;

    // The ID of the passenger giving the rating
    private Long raterPersonId;

    public RatingRequest(Long ratedPersonId, double rating, Long raterPersonId) {
        this.ratedPersonId = ratedPersonId;
        this.rating = rating;
        this.raterPersonId = raterPersonId;
    }

    // Getters are required for Gson to convert this object to JSON
    public Long getRatedPersonId() {
        return ratedPersonId;
    }

    public double getRating() {
        return rating;
    }

    public Long getRaterPersonId() {
        return raterPersonId;
    }
}