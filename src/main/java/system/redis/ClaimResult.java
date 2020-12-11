package system.redis;

import lombok.Data;

@Data
public class ClaimResult {

    private Integer userId;

    private Integer couponId;

    private Integer claimedNum;

    public ClaimResult(int couponId , int userId, int claimedNum){
        this.couponId = couponId;
        this.userId = userId;
        this.claimedNum = claimedNum;
    }
}
