package shipping;

/**
 * Hệ thống tính phí vận chuyển nội địa
 *
 * Quy tắc tính phí:
 * - weight (kg): 0.1 <= weight <= 50.0
 * - distance (km): 1 <= distance <= 2000
 *
 * Phí cơ bản theo trọng lượng:
 *   0.1 <= weight <= 1.0  : 15,000 VND
 *   1.0 < weight <= 5.0   : 25,000 VND
 *   5.0 < weight <= 20.0  : 45,000 VND
 *   20.0 < weight <= 50.0 : 80,000 VND
 *
 * Hệ số khoảng cách (nhân với phí cơ bản):
 *   1 <= distance <= 100  : x1.0
 *   100 < distance <= 500 : x1.5
 *   500 < distance <= 2000: x2.5
 *
 * Nếu weight > 30 VÀ distance > 500 → cộng thêm phụ phí nặng 20,000 VND
 * Đầu ra: tổng phí (VND) hoặc ném IllegalArgumentException nếu đầu vào không hợp lệ
 */
public class ShippingFeeCalculator {

    public static final double MIN_WEIGHT = 0.1;
    public static final double MAX_WEIGHT = 50.0;
    public static final int MIN_DISTANCE = 1;
    public static final int MAX_DISTANCE = 2000;

    /**
     * Tính phí vận chuyển
     *
     * @param weight   trọng lượng gói hàng (kg), 0.1 <= weight <= 50.0
     * @param distance khoảng cách vận chuyển (km), 1 <= distance <= 2000
     * @return tổng phí vận chuyển (VND)
     * @throws IllegalArgumentException nếu đầu vào nằm ngoài miền hợp lệ
     */
    public double calculateFee(double weight, int distance) {
        validateInputs(weight, distance);

        double baseFee = getBaseFee(weight);
        double distanceFactor = getDistanceFactor(distance);
        double totalFee = baseFee * distanceFactor;

        if (isHeavySurchargeApplicable(weight, distance)) {
            totalFee += 20000;
        }

        return totalFee;
    }

    private void validateInputs(double weight, int distance) {
        if (weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
            throw new IllegalArgumentException(
                "Trọng lượng không hợp lệ: " + weight +
                ". Phải trong khoảng [" + MIN_WEIGHT + ", " + MAX_WEIGHT + "] kg"
            );
        }
        if (distance < MIN_DISTANCE || distance > MAX_DISTANCE) {
            throw new IllegalArgumentException(
                "Khoảng cách không hợp lệ: " + distance +
                ". Phải trong khoảng [" + MIN_DISTANCE + ", " + MAX_DISTANCE + "] km"
            );
        }
    }

    private double getBaseFee(double weight) {
        if (weight <= 1.0) {
            return 15000;
        } else if (weight <= 5.0) {
            return 25000;
        } else if (weight <= 20.0) {
            return 45000;
        } else {
            return 80000;
        }
    }

    private double getDistanceFactor(int distance) {
        if (distance <= 100) {
            return 1.0;
        } else if (distance <= 500) {
            return 1.5;
        } else {
            return 2.5;
        }
    }

    private boolean isHeavySurchargeApplicable(double weight, int distance) {
        return weight > 30.0 && distance > 500;
    }
}
