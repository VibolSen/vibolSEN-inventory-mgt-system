package com.vibolSEN.inventory_mgt_system.config;

import com.vibolSEN.inventory_mgt_system.model.Category;
import com.vibolSEN.inventory_mgt_system.model.Product;
import com.vibolSEN.inventory_mgt_system.model.enums.ProductStatus;
import com.vibolSEN.inventory_mgt_system.repository.CategoryRepository;
import com.vibolSEN.inventory_mgt_system.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, Category> categoryMap = seedCategories();
        seedProducts(categoryMap);
    }

    private Map<String, Category> seedCategories() {
        String[][] categoryData = {
                {"Electronics", "Electronic gadgets, mobile devices, and power accessories"},
                {"Computers & Laptops", "High-performance laptops, desktops, and workstations"},
                {"Computer Accessories", "Keyboards, mice, monitors, hubs, and docking stations"},
                {"Audio & Sound", "Headphones, wireless earbuds, soundbars, and microphones"},
                {"Gaming Gear", "Gaming consoles, mechanical keyboards, and gaming accessories"},
                {"Office Supplies", "Office stationery, printers, paper shredders, and organizers"},
                {"Networking Equipment", "Routers, network switches, access points, and cables"},
                {"Wearable Technology", "Smartwatches, fitness bands, and wearable sensors"},
                {"Cameras & Photography", "DSLRs, mirrorless cameras, lenses, and tripods"},
                {"Storage & Memory", "Internal & external SSDs, USB flash drives, and RAM modules"}
        };

        Map<String, Category> categoryMap = new HashMap<>();
        for (String[] data : categoryData) {
            String name = data[0];
            String description = data[1];
            Category category = categoryRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> categoryRepository.save(Category.builder()
                            .name(name)
                            .description(description)
                            .build()));
            categoryMap.put(name, category);
        }
        log.info("Initialized {} categories", categoryMap.size());
        return categoryMap;
    }

    private void seedProducts(Map<String, Category> categoryMap) {
        if (productRepository.count() >= 100) {
            log.info("Database already contains {} products. Skipping seeding.", productRepository.count());
            return;
        }

        List<ProductSeedData> seedList = getProductSeedList();
        List<Product> productsToSave = new ArrayList<>();

        int index = 1;
        for (ProductSeedData item : seedList) {
            String sku = String.format("SKU-PROD-%03d", index);
            String barcode = String.format("885100000%03d", index);

            if (!productRepository.existsBySkuIgnoreCase(sku)) {
                Category cat = categoryMap.get(item.categoryName());
                if (cat == null) {
                    cat = categoryMap.values().iterator().next();
                }

                ProductStatus status = item.stockQuantity() == 0 ? ProductStatus.OUT_OF_STOCK : item.status();

                Product product = Product.builder()
                        .name(item.name())
                        .sku(sku)
                        .barcode(barcode)
                        .description(item.description())
                        .costPrice(BigDecimal.valueOf(item.costPrice()))
                        .unitPrice(BigDecimal.valueOf(item.unitPrice()))
                        .stockQuantity(item.stockQuantity())
                        .minStockLevel(item.minStockLevel())
                        .unitOfMeasure(item.unitOfMeasure())
                        .status(status)
                        .category(cat)
                        .supplierId((long) ((index % 5) + 1))
                        .build();

                productsToSave.add(product);
            }
            index++;
        }

        if (!productsToSave.isEmpty()) {
            productRepository.saveAll(productsToSave);
            log.info("Successfully seeded {} test products into database. Total products now: {}",
                    productsToSave.size(), productRepository.count());
        }
    }

    private record ProductSeedData(
            String categoryName,
            String name,
            String description,
            double costPrice,
            double unitPrice,
            int stockQuantity,
            int minStockLevel,
            String unitOfMeasure,
            ProductStatus status
    ) {}

    private List<ProductSeedData> getProductSeedList() {
        List<ProductSeedData> list = new ArrayList<>();

        // 1. Electronics (1-10)
        list.add(new ProductSeedData("Electronics", "Apple iPhone 15 Pro 128GB", "Titanium design, A17 Pro chip, 48MP camera", 820.00, 999.00, 45, 10, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Electronics", "Samsung Galaxy S24 Ultra 256GB", "Titanium frame, Snapdragon 8 Gen 3, S-Pen included", 880.00, 1199.00, 30, 8, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Electronics", "Google Pixel 8 Pro 128GB", "Google Tensor G3 chip, advanced AI photography", 680.00, 899.00, 25, 5, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Electronics", "Anker 737 Power Bank 24000mAh", "140W fast output power bank with smart digital display", 90.00, 149.99, 80, 15, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Electronics", "Belkin MagSafe 3-in-1 Wireless Charger", "15W fast charging for iPhone, Apple Watch, and AirPods", 85.00, 129.99, 40, 10, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Electronics", "Ugreen 100W GaN Fast Wall Charger", "4-port USB-C GaN fast desktop charger", 45.00, 74.99, 120, 20, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Electronics", "Amazon Echo Dot 5th Gen", "Smart speaker with Alexa and vibrant sound", 28.00, 49.99, 65, 15, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Electronics", "Xiaomi Smart Air Purifier 4", "High-efficiency filtration for clean, allergen-free air", 110.00, 169.99, 18, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Electronics", "Philips Hue White & Color Ambiance Bulb", "Smart LED bulb compatible with Zigbee & Bluetooth", 30.00, 49.99, 90, 15, "BOX", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Electronics", "TP-Link Kasa Smart Wi-Fi Plug Mini", "Compact smart plug with energy monitoring and timer", 12.00, 19.99, 150, 25, "BOX", ProductStatus.ACTIVE));

        // 2. Computers & Laptops (11-20)
        list.add(new ProductSeedData("Computers & Laptops", "Apple MacBook Air 13-inch M3", "Liquid Retina display, 8-core CPU, 10-core GPU, 8GB RAM", 850.00, 1099.00, 35, 8, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computers & Laptops", "Apple MacBook Pro 16-inch M3 Max", "36GB unified memory, 1TB SSD, Liquid Retina XDR", 2800.00, 3499.00, 12, 3, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computers & Laptops", "Dell XPS 15 9530 Laptop", "Intel Core i9-13900H, RTX 4070, 32GB RAM, 1TB SSD", 1950.00, 2499.00, 15, 4, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computers & Laptops", "Lenovo ThinkPad X1 Carbon Gen 11", "Intel Core i7-1365U, 16GB RAM, 512GB SSD, ultralight", 1250.00, 1649.00, 22, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computers & Laptops", "HP Spectre x360 14 2-in-1", "Intel Core Ultra 7, OLED touchscreen, 16GB RAM", 1100.00, 1449.00, 18, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computers & Laptops", "Asus Zenbook 14 OLED", "Intel Core Ultra 7 155H, 16GB RAM, 1TB SSD, 3K OLED 120Hz", 850.00, 1099.00, 28, 6, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computers & Laptops", "Microsoft Surface Laptop 5 13.5-inch", "Intel Core i7, 16GB RAM, 512GB SSD, touchscreen", 1050.00, 1399.00, 14, 4, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computers & Laptops", "Apple Mac Mini M2 Pro", "10-core CPU, 16-core GPU, 16GB RAM, 512GB SSD", 1000.00, 1299.00, 20, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computers & Laptops", "Dell OptiPlex 7010 Micro Desktop", "Intel Core i5-13500T, 16GB RAM, 512GB NVMe SSD", 550.00, 749.00, 30, 8, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computers & Laptops", "Lenovo IdeaCentre 5i Tower Desktop", "Intel Core i7-13700, 16GB RAM, 1TB SSD, Intel UHD 770", 680.00, 899.00, 16, 4, "UNIT", ProductStatus.ACTIVE));

        // 3. Computer Accessories (21-30)
        list.add(new ProductSeedData("Computer Accessories", "Logitech MX Master 3S Wireless Mouse", "8K DPI sensor, Quiet clicks, MagSpeed electromagnetic scroll", 65.00, 99.99, 85, 15, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computer Accessories", "Logitech MX Mechanical Wireless Keyboard", "Low-profile mechanical switches, tactile quiet, backlit", 110.00, 169.99, 50, 10, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computer Accessories", "Dell UltraSharp U2723QE 27 4K Monitor", "IPS Black technology, USB-C hub with 90W power delivery", 420.00, 579.99, 20, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computer Accessories", "LG 34WN80C-B 34 Curved UltraWide Monitor", "WQHD 3440x1440 IPS display with USB Type-C 60W", 390.00, 549.99, 15, 4, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computer Accessories", "Anker 575 USB-C Docking Station (13-in-1)", "Triple display support with 85W high-speed laptop charging", 140.00, 199.99, 40, 8, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computer Accessories", "Logitech Brio 4K Ultra HD Webcam", "HDR, RightLight 3, dual omni-directional noise-canceling mics", 120.00, 169.99, 60, 12, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computer Accessories", "CalDigit TS4 Thunderbolt 4 Dock", "18 ports of connectivity with 98W host power delivery", 310.00, 399.95, 18, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computer Accessories", "Keychron Q1 Pro Wireless Custom Keyboard", "QMK/VIA wireless mechanical keyboard, CNC aluminum frame", 140.00, 199.00, 32, 6, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computer Accessories", "Apple Magic Trackpad - Black", "Wireless, rechargeable, Multi-Touch gestures and Force Touch", 105.00, 149.00, 45, 10, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Computer Accessories", "SteelSeries QcK Heavy XXL Gaming Mouse Pad", "Extra thick non-slip rubber base, micro-woven cloth", 18.00, 29.99, 110, 20, "PCS", ProductStatus.ACTIVE));

        // 4. Audio & Sound (31-40)
        list.add(new ProductSeedData("Audio & Sound", "Sony WH-1000XM5 Wireless Noise Canceling Headphones", "Industry leading active noise cancellation, 30h battery", 260.00, 399.99, 42, 8, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Audio & Sound", "Bose QuietComfort Ultra Headphones", "Spatial audio, world-class noise cancellation, custom sound", 280.00, 429.00, 30, 6, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Audio & Sound", "Apple AirPods Pro 2nd Gen USB-C", "Active noise cancellation, Adaptive Audio, MagSafe USB-C case", 180.00, 249.00, 75, 15, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Audio & Sound", "Sennheiser Momentum 4 Wireless", "Audiophile-inspired acoustics, 60-hour battery life", 220.00, 349.95, 25, 5, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Audio & Sound", "Shure SM7B Vocal Dynamic Microphone", "Legendary dynamic cardioid studio vocal microphone", 310.00, 399.00, 20, 4, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Audio & Sound", "Rode Wireless PRO Dual Microphone System", "32-bit float on-board recording, timecode sync, Lavalier mics", 320.00, 399.00, 15, 3, "SET", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Audio & Sound", "JBL Flip 6 Portable Bluetooth Speaker", "IP67 waterproof and dustproof, 12 hours playtime", 75.00, 129.95, 95, 18, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Audio & Sound", "Sonos Beam Gen 2 Compact Smart Soundbar", "Dolby Atmos panoramic sound, crystal clear dialogue", 340.00, 499.00, 14, 3, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Audio & Sound", "Audio-Technica ATH-M50x Studio Monitor Headphones", "Exceptional clarity, extended frequency range, deep bass", 95.00, 149.00, 60, 12, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Audio & Sound", "Blue Yeti USB Microphone - Blackout", "Custom three-capsule array, 4 pickup patterns, plug and play", 80.00, 129.99, 55, 10, "PCS", ProductStatus.ACTIVE));

        // 5. Gaming Gear (41-50)
        list.add(new ProductSeedData("Gaming Gear", "Sony PlayStation 5 Slim Console", "1TB SSD storage, ultra-high speed, ray tracing support", 410.00, 499.99, 28, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Gaming Gear", "Microsoft Xbox Series X Console", "12 teraflops raw graphic processing power, 1TB SSD", 410.00, 499.99, 20, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Gaming Gear", "Nintendo Switch OLED Model - White", "7-inch vibrant OLED screen, wide adjustable stand, 64GB", 280.00, 349.99, 38, 8, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Gaming Gear", "Razer DeathAdder V3 Pro Wireless", "63g ultra-lightweight esports ergonomic wireless mouse", 95.00, 149.99, 65, 12, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Gaming Gear", "Razer Huntsman V3 Pro Mechanical Keyboard", "Analog optical switches, rapid trigger, aluminum top plate", 170.00, 249.99, 30, 6, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Gaming Gear", "HyperX Cloud III Wireless Gaming Headset", "Up to 120-hour battery, 53mm angled drivers, ultra comfort", 110.00, 169.99, 45, 10, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Gaming Gear", "Sony DualSense Edge Wireless Controller", "High-performance customizable PS5 gamepad with back buttons", 150.00, 199.99, 32, 6, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Gaming Gear", "Xbox Elite Wireless Controller Series 2", "Adjustable tension thumbsticks, wrap-around rubberized grip", 130.00, 179.99, 25, 5, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Gaming Gear", "Secretlab TITAN Evo Gaming Chair - Stealth", "Ergonomic magnetic memory foam head pillow, 4-way lumbar", 410.00, 549.00, 8, 2, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Gaming Gear", "Elgato Stream Deck MK.2", "15 customizable LCD keys for livestreaming and macro actions", 100.00, 149.99, 50, 10, "PCS", ProductStatus.ACTIVE));

        // 6. Office Supplies (51-60)
        list.add(new ProductSeedData("Office Supplies", "HP LaserJet Pro MFP 3101fdw Printer", "Wireless multifunction monochrome laser printer with fax", 190.00, 259.99, 15, 3, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Office Supplies", "Epson EcoTank ET-2850 All-in-One Cartridge-Free", "High-capacity ink tanks with auto 2-sided printing", 210.00, 299.99, 18, 4, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Office Supplies", "Fellowes Powershred 79Ci Cross-Cut Paper Shredder", "100% jam proof system, 16 sheet shredding capacity", 160.00, 229.99, 12, 3, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Office Supplies", "Brother P-touch Cube Plus Label Maker", "Bluetooth wireless label maker with rechargeable Li-ion battery", 70.00, 99.99, 40, 8, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Office Supplies", "Hammermill Copy Plus Paper 20lb Letter Case", "8.5 x 11, 500 sheets/ream, 10 reams case (5000 sheets)", 38.00, 54.99, 150, 30, "BOX", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Office Supplies", "Pilot G2 Premium Gel Ink Pens 0.7mm Black (12-Pack)", "Smooth-writing, longest-lasting gel ink pens", 9.50, 15.99, 220, 40, "BOX", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Office Supplies", "Post-it Super Sticky Notes 3x3 Canary Yellow (12-Pack)", "Twice the sticking power, holds stronger and lasts longer", 12.00, 19.99, 180, 30, "PACK", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Office Supplies", "Scotch Heavy Duty Packaging Tape (6-Pack)", "Strong solvent-free adhesive holds down up to 80 lbs", 14.00, 22.99, 130, 25, "PACK", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Office Supplies", "Bostitch Office Heavy Duty Stapler 40-Sheet", "No-jam technology, spring-powered stapler with 5000 staples", 15.00, 24.99, 85, 15, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Office Supplies", "Deflecto Sustainable Ergonomic Desk Organizer", "Multi-compartment storage caddy for desktop office tools", 11.00, 18.99, 90, 15, "PCS", ProductStatus.ACTIVE));

        // 7. Networking Equipment (61-70)
        list.add(new ProductSeedData("Networking Equipment", "Ubiquiti UniFi Dream Machine Special Edition", "Enterprise-grade all-in-one router, gateway, PoE switch", 410.00, 499.00, 10, 2, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Networking Equipment", "Ubiquiti UniFi U6 Pro Wi-Fi 6 Access Point", "Dual-band Wi-Fi 6, 5.3 Gbps aggregate throughput, PoE", 125.00, 159.00, 35, 8, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Networking Equipment", "ASUS RT-AX88U Pro Dual-Band Wi-Fi 6 Router", "Dual 2.5G ports, quad-core 2.0 GHz CPU, AiMesh support", 220.00, 299.99, 22, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Networking Equipment", "TP-Link Deco XE75 Pro AXE5400 Mesh System (3-Pack)", "Tri-band Wi-Fi 6E mesh coverage up to 7200 sq.ft", 280.00, 399.99, 15, 3, "SET", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Networking Equipment", "Netgear GS108 8-Port Gigabit Ethernet Unmanaged Switch", "ProSAFE plug-and-play desktop metal housing switch", 25.00, 39.99, 110, 20, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Networking Equipment", "Cisco CBS110-16T Unmanaged 16-Port Gigabit Switch", "16 x 10/100/1000 ports with energy efficient ethernet", 95.00, 139.99, 24, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Networking Equipment", "Cat 6 Ethernet Patch Cable 50ft Snagless", "550MHz UTP bare copper wire RJ45 network cable", 8.00, 14.99, 160, 30, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Networking Equipment", "Synology DiskStation DS224+ 2-Bay NAS", "Intel Celeron J4125 4-core, 2GB DDR4 expandable to 6GB", 250.00, 319.99, 16, 4, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Networking Equipment", "MikroTik hEX RB750Gr3 5-Port Gigabit Router", "Dual core 880MHz CPU, 256MB RAM, RouterOS L4 license", 42.00, 59.95, 45, 10, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Networking Equipment", "Netgear Nighthawk M6 Pro 5G Mobile Hotspot Router", "Wi-Fi 6E, mmWave 5G connectivity, up to 8 Gbps speeds", 680.00, 899.99, 8, 2, "UNIT", ProductStatus.ACTIVE));

        // 8. Wearable Technology (71-80)
        list.add(new ProductSeedData("Wearable Technology", "Apple Watch Ultra 2 GPS + Cellular 49mm", "Rugged titanium case, precision dual-frequency GPS, 3000 nits", 650.00, 799.00, 18, 4, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Wearable Technology", "Apple Watch Series 9 GPS 45mm", "S9 SiP, Double tap gesture, brighter display, ECG sensor", 340.00, 429.00, 35, 8, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Wearable Technology", "Samsung Galaxy Watch 6 Classic 47mm", "Rotating bezel, advanced sleep coaching, BIA body analysis", 290.00, 399.99, 28, 6, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Wearable Technology", "Garmin Fenix 7 Pro Solar Multisport GPS Watch", "Solar charging lens, built-in LED flashlight, endurance score", 620.00, 799.99, 14, 3, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Wearable Technology", "Fitbit Charge 6 Fitness Tracker", "Built-in GPS, heart rate on gym equipment, 40+ exercise modes", 105.00, 159.95, 55, 12, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Wearable Technology", "Meta Quest 3 128GB VR Headset", "Mixed reality, 4K+ Infinite Display, Touch Plus controllers", 410.00, 499.99, 22, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Wearable Technology", "Oura Ring Gen 3 Horizon - Stealth Black", "Smart ring health tracker for sleep, readiness, and heart rate", 290.00, 399.00, 20, 4, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Wearable Technology", "WHOOP 4.0 Health & Fitness Tracker Band", "24/7 continuous health monitoring with strain & recovery metrics", 190.00, 239.00, 30, 6, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Wearable Technology", "Garmin Forerunner 265 Running Smartwatch", "AMOLED touchscreen display, training readiness, HRV status", 340.00, 449.99, 16, 3, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Wearable Technology", "Ray-Ban Meta Wayfarer Smart Glasses", "12 MP camera, open-ear audio, Meta AI assistant integration", 230.00, 299.00, 25, 5, "PCS", ProductStatus.ACTIVE));

        // 9. Cameras & Photography (81-90)
        list.add(new ProductSeedData("Cameras & Photography", "Sony Alpha a7 IV Full-Frame Mirrorless Camera", "33MP Exmor R sensor, 4K 60p, real-time eye AF for photo/video", 2000.00, 2498.00, 8, 2, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Cameras & Photography", "Canon EOS R6 Mark II Mirrorless Camera Body", "24.2MP full frame, 40 fps electronic shutter, 4K 60p 6K oversampling", 1950.00, 2399.00, 7, 2, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Cameras & Photography", "Fujifilm X-T5 Mirrorless Camera Body - Black", "40.2MP X-Trans CMOS 5 HR sensor, 5-axis in-body stabilization", 1350.00, 1699.95, 10, 2, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Cameras & Photography", "Sony FE 24-70mm f/2.8 GM II Lens", "G Master standard zoom lens, fast f/2.8 constant aperture", 1850.00, 2298.00, 6, 2, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Cameras & Photography", "Canon RF 50mm f/1.8 STM Lens", "Compact, lightweight normal prime lens for EOS R cameras", 140.00, 199.00, 45, 10, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Cameras & Photography", "DJI Mini 4 Pro Drone with RC 2 Controller", "Under 249g, 4K 60fps HDR video, omnidirectional obstacle sensing", 620.00, 759.00, 15, 3, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Cameras & Photography", "GoPro HERO12 Black Action Camera", "5.3K 60 video, HyperSmooth 6.0 stabilization, waterproof to 33ft", 280.00, 399.99, 40, 8, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Cameras & Photography", "DJI Osmo Pocket 3 Gimbal Camera", "1-inch CMOS pocket-sized camera with 4K 120fps and 2-inch rotatable OLED", 420.00, 519.00, 22, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Cameras & Photography", "Peak Design Everyday Backpack 20L v2", "Versatile camera and laptop backpack with weatherproof zippers", 200.00, 279.95, 25, 5, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Cameras & Photography", "Manfrotto Befree Advanced Carbon Fiber Tripod", "Lightweight travel tripod with ball head, 19.8 lb payload", 260.00, 349.88, 14, 3, "PCS", ProductStatus.ACTIVE));

        // 10. Storage & Memory (91-100)
        list.add(new ProductSeedData("Storage & Memory", "Samsung 990 PRO 2TB PCIe 4.0 M.2 NVMe SSD", "Up to 7,450 MB/s sequential read, heatsink model", 140.00, 199.99, 70, 15, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Storage & Memory", "Samsung 990 PRO 4TB PCIe 4.0 M.2 NVMe SSD", "High-capacity lightning fast NVMe SSD for gamers & creators", 260.00, 349.99, 35, 8, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Storage & Memory", "Crucial T700 2TB Gen5 NVMe M.2 SSD", "PCIe 5.0 speed up to 12,400 MB/s read, premium aluminum heatsink", 220.00, 299.99, 20, 5, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Storage & Memory", "SanDisk 2TB Extreme Portable SSD", "Up to 1050 MB/s read, IP65 water and dust resistance", 110.00, 159.99, 85, 18, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Storage & Memory", "Samsung T7 Shield 4TB Portable SSD", "Rugged external solid state drive, USB 3.2 Gen 2, drop resistant", 230.00, 319.99, 30, 6, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Storage & Memory", "Western Digital 12TB Elements Desktop Hard Drive", "High-capacity plug-and-play USB 3.0 desktop storage", 180.00, 249.99, 25, 5, "UNIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Storage & Memory", "Seagate IronWolf Pro 16TB NAS Hard Drive", "7200 RPM, 256MB cache, CMR for multi-bay RAID systems", 260.00, 329.99, 18, 4, "PCS", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Storage & Memory", "Corsair Vengeance DDR5 32GB (2x16GB) 6000MHz", "Intel XMP 3.0 optimized high performance desktop RAM kit", 85.00, 119.99, 60, 12, "KIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Storage & Memory", "G.SKILL Trident Z5 RGB 64GB (2x32GB) DDR5 6400MHz", "Ultra-fast dual-channel DDR5 kit with customizable RGB lighting", 160.00, 229.99, 28, 6, "KIT", ProductStatus.ACTIVE));
        list.add(new ProductSeedData("Storage & Memory", "SanDisk Extreme PRO 256GB SDXC UHS-I Card", "Up to 200 MB/s read speed, 4K UHD video recording V30", 30.00, 48.99, 140, 25, "PCS", ProductStatus.ACTIVE));

        return list;
    }
}
