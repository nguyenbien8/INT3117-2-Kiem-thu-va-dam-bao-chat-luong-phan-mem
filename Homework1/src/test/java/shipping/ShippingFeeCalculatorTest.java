package shipping;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử hộp đen cho ShippingFeeCalculator
 *
 * Gồm 2 nhóm kiểm thử:
 *   1. BVT  - Boundary Value Testing (Kiểm thử giá trị biên)
 *   2. DT   - Decision Table Testing (Kiểm thử bảng quyết định)
 */
@DisplayName("ShippingFeeCalculator - Black-Box Testing")
public class ShippingFeeCalculatorTest {

    private ShippingFeeCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ShippingFeeCalculator();
    }

    // =========================================================================
    // NHÓM 1: KIỂM THỬ GIÁ TRỊ BIÊN (BOUNDARY VALUE TESTING)
    // Biến: weight [0.1, 50.0], distance [1, 2000]
    // Phương pháp: giữ một biến ở nom, thay đổi biến còn lại theo min/min+/nom/max-/max
    // nom_weight = 10.0 kg, nom_distance = 500 km
    // =========================================================================

    @Nested
    @DisplayName("BVT - Kiểm thử giá trị biên biến weight (distance = nom = 500)")
    class BoundaryValueWeight {

        @Test
        @DisplayName("BVT-W01: weight = min = 0.1 kg, distance = 500")
        void bvt_W01_weightMin() {
            // weight=0.1 -> baseFee=15000, distance=500 -> factor=1.5, no surcharge
            assertEquals(22500.0, calculator.calculateFee(0.1, 500), 0.01);
        }

        @Test
        @DisplayName("BVT-W02: weight = min+ = 0.2 kg, distance = 500")
        void bvt_W02_weightMinPlus() {
            assertEquals(22500.0, calculator.calculateFee(0.2, 500), 0.01);
        }

        @Test
        @DisplayName("BVT-W03: weight = nom = 10.0 kg, distance = 500")
        void bvt_W03_weightNom() {
            // weight=10.0 -> baseFee=45000, distance=500 -> factor=1.5
            assertEquals(67500.0, calculator.calculateFee(10.0, 500), 0.01);
        }

        @Test
        @DisplayName("BVT-W04: weight = max- = 49.9 kg, distance = 500")
        void bvt_W04_weightMaxMinus() {
            // weight=49.9 -> baseFee=80000, distance=500 -> factor=1.5, no surcharge (distance<=500)
            assertEquals(120000.0, calculator.calculateFee(49.9, 500), 0.01);
        }

        @Test
        @DisplayName("BVT-W05: weight = max = 50.0 kg, distance = 500")
        void bvt_W05_weightMax() {
            assertEquals(120000.0, calculator.calculateFee(50.0, 500), 0.01);
        }
    }

    @Nested
    @DisplayName("BVT - Kiểm thử giá trị biên biến distance (weight = nom = 10.0)")
    class BoundaryValueDistance {

        @Test
        @DisplayName("BVT-D01: distance = min = 1 km, weight = 10.0")
        void bvt_D01_distanceMin() {
            // weight=10.0 -> baseFee=45000, distance=1 -> factor=1.0
            assertEquals(45000.0, calculator.calculateFee(10.0, 1), 0.01);
        }

        @Test
        @DisplayName("BVT-D02: distance = min+ = 2 km, weight = 10.0")
        void bvt_D02_distanceMinPlus() {
            assertEquals(45000.0, calculator.calculateFee(10.0, 2), 0.01);
        }

        @Test
        @DisplayName("BVT-D03: distance = nom = 500 km, weight = 10.0")
        void bvt_D03_distanceNom() {
            // distance=500 -> factor=1.5
            assertEquals(67500.0, calculator.calculateFee(10.0, 500), 0.01);
        }

        @Test
        @DisplayName("BVT-D04: distance = max- = 1999 km, weight = 10.0")
        void bvt_D04_distanceMaxMinus() {
            // distance=1999 -> factor=2.5
            assertEquals(112500.0, calculator.calculateFee(10.0, 1999), 0.01);
        }

        @Test
        @DisplayName("BVT-D05: distance = max = 2000 km, weight = 10.0")
        void bvt_D05_distanceMax() {
            assertEquals(112500.0, calculator.calculateFee(10.0, 2000), 0.01);
        }
    }

    @Nested
    @DisplayName("BVT - Kiểm thử giá trị biên mạnh (ngoài miền hợp lệ)")
    class RobustBoundaryValue {

        @Test
        @DisplayName("BVT-R01: weight = min- = 0.09 (dưới min) -> Exception")
        void bvt_R01_weightBelowMin() {
            assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(0.09, 500));
        }

        @Test
        @DisplayName("BVT-R02: weight = max+ = 50.1 (trên max) -> Exception")
        void bvt_R02_weightAboveMax() {
            assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(50.1, 500));
        }

        @Test
        @DisplayName("BVT-R03: distance = min- = 0 (dưới min) -> Exception")
        void bvt_R03_distanceBelowMin() {
            assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(10.0, 0));
        }

        @Test
        @DisplayName("BVT-R04: distance = max+ = 2001 (trên max) -> Exception")
        void bvt_R04_distanceAboveMax() {
            assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(10.0, 2001));
        }

        @Test
        @DisplayName("BVT-R05: weight âm -> Exception")
        void bvt_R05_weightNegative() {
            assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(-1.0, 500));
        }

        @Test
        @DisplayName("BVT-R06: distance âm -> Exception")
        void bvt_R06_distanceNegative() {
            assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(10.0, -100));
        }
    }

    // =========================================================================
    // NHÓM 2: KIỂM THỬ BẢNG QUYẾT ĐỊNH (DECISION TABLE TESTING)
    //
    // Điều kiện:
    //   C1: weight <= 1.0
    //   C2: 1.0 < weight <= 5.0
    //   C3: 5.0 < weight <= 20.0
    //   C4: 20.0 < weight <= 50.0
    //   C5: distance <= 100
    //   C6: 100 < distance <= 500
    //   C7: 500 < distance <= 2000
    //   C8: weight > 30 AND distance > 500 (phụ phí nặng)
    //
    // Hành động:
    //   E1: baseFee = 15,000
    //   E2: baseFee = 25,000
    //   E3: baseFee = 45,000
    //   E4: baseFee = 80,000
    //   E5: factor = 1.0
    //   E6: factor = 1.5
    //   E7: factor = 2.5
    //   E8: surcharge = +20,000
    //   E9: Exception
    // =========================================================================

    @Nested
    @DisplayName("DT - Kiểm thử bảng quyết định (12 luật hợp lệ)")
    class DecisionTable {

        // Rule 1: C1+C5 => baseFee=15000, factor=1.0 => 15,000
        @Test
        @DisplayName("DT-R01: weight<=1, distance<=100 → 15,000")
        void dt_R01() {
            assertEquals(15000.0, calculator.calculateFee(0.5, 50), 0.01);
        }

        // Rule 2: C1+C6 => baseFee=15000, factor=1.5 => 22,500
        @Test
        @DisplayName("DT-R02: weight<=1, 100<distance<=500 → 22,500")
        void dt_R02() {
            assertEquals(22500.0, calculator.calculateFee(1.0, 300), 0.01);
        }

        // Rule 3: C1+C7 => baseFee=15000, factor=2.5 => 37,500
        @Test
        @DisplayName("DT-R03: weight<=1, distance>500 → 37,500")
        void dt_R03() {
            assertEquals(37500.0, calculator.calculateFee(0.5, 1000), 0.01);
        }

        // Rule 4: C2+C5 => baseFee=25000, factor=1.0 => 25,000
        @Test
        @DisplayName("DT-R04: 1<weight<=5, distance<=100 → 25,000")
        void dt_R04() {
            assertEquals(25000.0, calculator.calculateFee(3.0, 80), 0.01);
        }

        // Rule 5: C2+C6 => baseFee=25000, factor=1.5 => 37,500
        @Test
        @DisplayName("DT-R05: 1<weight<=5, 100<distance<=500 → 37,500")
        void dt_R05() {
            assertEquals(37500.0, calculator.calculateFee(5.0, 200), 0.01);
        }

        // Rule 6: C2+C7 => baseFee=25000, factor=2.5 => 62,500
        @Test
        @DisplayName("DT-R06: 1<weight<=5, distance>500 → 62,500")
        void dt_R06() {
            assertEquals(62500.0, calculator.calculateFee(4.0, 800), 0.01);
        }

        // Rule 7: C3+C5 => baseFee=45000, factor=1.0 => 45,000
        @Test
        @DisplayName("DT-R07: 5<weight<=20, distance<=100 → 45,000")
        void dt_R07() {
            assertEquals(45000.0, calculator.calculateFee(10.0, 100), 0.01);
        }

        // Rule 8: C3+C6 => baseFee=45000, factor=1.5 => 67,500
        @Test
        @DisplayName("DT-R08: 5<weight<=20, 100<distance<=500 → 67,500")
        void dt_R08() {
            assertEquals(67500.0, calculator.calculateFee(15.0, 400), 0.01);
        }

        // Rule 9: C3+C7 => baseFee=45000, factor=2.5 => 112,500
        @Test
        @DisplayName("DT-R09: 5<weight<=20, distance>500 → 112,500")
        void dt_R09() {
            assertEquals(112500.0, calculator.calculateFee(20.0, 600), 0.01);
        }

        // Rule 10: C4+C5, weight<=30 => baseFee=80000, factor=1.0 => 80,000
        @Test
        @DisplayName("DT-R10: 20<weight<=30, distance<=100 → 80,000")
        void dt_R10() {
            assertEquals(80000.0, calculator.calculateFee(25.0, 50), 0.01);
        }

        // Rule 11: C4+C6, weight<=30 => baseFee=80000, factor=1.5 => 120,000
        @Test
        @DisplayName("DT-R11: 20<weight<=30, 100<distance<=500 → 120,000")
        void dt_R11() {
            assertEquals(120000.0, calculator.calculateFee(30.0, 300), 0.01);
        }

        // Rule 12: C4+C7, weight<=30 => baseFee=80000, factor=2.5 => 200,000 (no surcharge, weight<=30)
        @Test
        @DisplayName("DT-R12: 20<weight<=30, distance>500 → 200,000 (không phụ phí)")
        void dt_R12() {
            assertEquals(200000.0, calculator.calculateFee(30.0, 1000), 0.01);
        }

        // Rule 13: C4+C5, weight>30 => baseFee=80000, factor=1.0 => 80,000 (no surcharge, dist<=100)
        @Test
        @DisplayName("DT-R13: 30<weight<=50, distance<=100 → 80,000 (không phụ phí)")
        void dt_R13() {
            assertEquals(80000.0, calculator.calculateFee(35.0, 50), 0.01);
        }

        // Rule 14: C4+C6, weight>30 => baseFee=80000, factor=1.5 => 120,000 (no surcharge, dist<=500)
        @Test
        @DisplayName("DT-R14: 30<weight<=50, 100<distance<=500 → 120,000 (không phụ phí)")
        void dt_R14() {
            assertEquals(120000.0, calculator.calculateFee(40.0, 400), 0.01);
        }

        // Rule 15: C4+C7, weight>30 => baseFee=80000, factor=2.5, +surcharge 20000 => 220,000
        @Test
        @DisplayName("DT-R15: 30<weight<=50, distance>500 → 220,000 (có phụ phí nặng)")
        void dt_R15() {
            assertEquals(220000.0, calculator.calculateFee(45.0, 1500), 0.01);
        }
    }

    @Nested
    @DisplayName("DT - Kiểm thử đầu vào không hợp lệ")
    class DecisionTableInvalid {

        @Test
        @DisplayName("DT-INV01: weight < min (0.09) → Exception")
        void dt_INV01() {
            Exception ex = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(0.09, 100));
            assertTrue(ex.getMessage().contains("Trọng lượng không hợp lệ"));
        }

        @Test
        @DisplayName("DT-INV02: weight > max (50.1) → Exception")
        void dt_INV02() {
            assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(50.1, 100));
        }

        @Test
        @DisplayName("DT-INV03: distance < min (0) → Exception")
        void dt_INV03() {
            Exception ex = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(10.0, 0));
            assertTrue(ex.getMessage().contains("Khoảng cách không hợp lệ"));
        }

        @Test
        @DisplayName("DT-INV04: distance > max (2001) → Exception")
        void dt_INV04() {
            assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(10.0, 2001));
        }
    }

    // =========================================================================
    // NHÓM 3: KIỂM THỬ CÁC GIÁ TRỊ BIÊN TẠI ĐƯỜNG PHÂN CHIA (RANH GIỚI)
    // Kiểm tra các điểm chính xác tại biên của các vùng phí
    // =========================================================================

    @Nested
    @DisplayName("BVT - Kiểm thử biên tại các điểm phân chia vùng phí")
    class BoundaryAtPartitions {

        @Test
        @DisplayName("BVT-P01: weight = 1.0 (biên W1/W2), distance = 50 → 15,000")
        void bvt_P01() {
            assertEquals(15000.0, calculator.calculateFee(1.0, 50), 0.01);
        }

        @Test
        @DisplayName("BVT-P02: weight = 1.01 (vào W2), distance = 50 → 25,000")
        void bvt_P02() {
            assertEquals(25000.0, calculator.calculateFee(1.01, 50), 0.01);
        }

        @Test
        @DisplayName("BVT-P03: weight = 5.0 (biên W2/W3), distance = 50 → 25,000")
        void bvt_P03() {
            assertEquals(25000.0, calculator.calculateFee(5.0, 50), 0.01);
        }

        @Test
        @DisplayName("BVT-P04: weight = 5.01 (vào W3), distance = 50 → 45,000")
        void bvt_P04() {
            assertEquals(45000.0, calculator.calculateFee(5.01, 50), 0.01);
        }

        @Test
        @DisplayName("BVT-P05: weight = 20.0 (biên W3/W4), distance = 50 → 45,000")
        void bvt_P05() {
            assertEquals(45000.0, calculator.calculateFee(20.0, 50), 0.01);
        }

        @Test
        @DisplayName("BVT-P06: weight = 20.01 (vào W4), distance = 50 → 80,000")
        void bvt_P06() {
            assertEquals(80000.0, calculator.calculateFee(20.01, 50), 0.01);
        }

        @Test
        @DisplayName("BVT-P07: distance = 100 (biên D1/D2), weight = 10.0 → 45,000")
        void bvt_P07() {
            assertEquals(45000.0, calculator.calculateFee(10.0, 100), 0.01);
        }

        @Test
        @DisplayName("BVT-P08: distance = 101 (vào D2), weight = 10.0 → 67,500")
        void bvt_P08() {
            assertEquals(67500.0, calculator.calculateFee(10.0, 101), 0.01);
        }

        @Test
        @DisplayName("BVT-P09: distance = 500 (biên D2/D3), weight = 10.0 → 67,500")
        void bvt_P09() {
            assertEquals(67500.0, calculator.calculateFee(10.0, 500), 0.01);
        }

        @Test
        @DisplayName("BVT-P10: distance = 501 (vào D3), weight = 10.0 → 112,500")
        void bvt_P10() {
            assertEquals(112500.0, calculator.calculateFee(10.0, 501), 0.01);
        }

        @Test
        @DisplayName("BVT-P11: weight = 30.0 (biên phụ phí), distance = 1000 → 200,000 (không phụ phí)")
        void bvt_P11() {
            // weight=30.0 không thỏa weight>30, nên không có phụ phí
            assertEquals(200000.0, calculator.calculateFee(30.0, 1000), 0.01);
        }

        @Test
        @DisplayName("BVT-P12: weight = 30.01 (vượt biên phụ phí), distance = 1000 → 220,000 (có phụ phí)")
        void bvt_P12() {
            // weight=30.01>30 AND distance=1000>500 → có phụ phí 20,000
            assertEquals(220000.0, calculator.calculateFee(30.01, 1000), 0.01);
        }
    }
}
