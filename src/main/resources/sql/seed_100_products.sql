-- ==========================================================
-- SQL Seed Script: 10 Categories and 100 Test Products
-- Database: inventory_mgt_db
-- ==========================================================

USE `inventory_mgt_db`;

-- 1. Insert 10 Categories
INSERT INTO `categories` (`name`, `description`, `created_at`, `updated_at`)
VALUES
('Electronics', 'Electronic gadgets, mobile devices, and power accessories', NOW(), NOW()),
('Computers & Laptops', 'High-performance laptops, desktops, and workstations', NOW(), NOW()),
('Computer Accessories', 'Keyboards, mice, monitors, hubs, and docking stations', NOW(), NOW()),
('Audio & Sound', 'Headphones, wireless earbuds, soundbars, and microphones', NOW(), NOW()),
('Gaming Gear', 'Gaming consoles, mechanical keyboards, and gaming accessories', NOW(), NOW()),
('Office Supplies', 'Office stationery, printers, paper shredders, and organizers', NOW(), NOW()),
('Networking Equipment', 'Routers, network switches, access points, and cables', NOW(), NOW()),
('Wearable Technology', 'Smartwatches, fitness bands, and wearable sensors', NOW(), NOW()),
('Cameras & Photography', 'DSLRs, mirrorless cameras, lenses, and tripods', NOW(), NOW()),
('Storage & Memory', 'Internal & external SSDs, USB flash drives, and RAM modules', NOW(), NOW())
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 2. Insert 100 Products
-- (Using category IDs 1 through 10 or subqueries for reliability)
INSERT INTO `products` (`name`, `sku`, `barcode`, `description`, `cost_price`, `unit_price`, `stock_quantity`, `min_stock_level`, `unit_of_measure`, `status`, `category_id`, `supplier_id`, `created_at`, `updated_at`)
VALUES
-- Electronics (Category 1)
('Apple iPhone 15 Pro 128GB', 'SKU-PROD-001', '885100000001', 'Titanium design, A17 Pro chip, 48MP camera', 820.00, 999.00, 45, 10, 'PCS', 'ACTIVE', 1, 1, NOW(), NOW()),
('Samsung Galaxy S24 Ultra 256GB', 'SKU-PROD-002', '885100000002', 'Titanium frame, Snapdragon 8 Gen 3, S-Pen included', 880.00, 1199.00, 30, 8, 'PCS', 'ACTIVE', 1, 2, NOW(), NOW()),
('Google Pixel 8 Pro 128GB', 'SKU-PROD-003', '885100000003', 'Google Tensor G3 chip, advanced AI photography', 680.00, 899.00, 25, 5, 'PCS', 'ACTIVE', 1, 3, NOW(), NOW()),
('Anker 737 Power Bank 24000mAh', 'SKU-PROD-004', '885100000004', '140W fast output power bank with smart digital display', 90.00, 149.99, 80, 15, 'UNIT', 'ACTIVE', 1, 4, NOW(), NOW()),
('Belkin MagSafe 3-in-1 Wireless Charger', 'SKU-PROD-005', '885100000005', '15W fast charging for iPhone, Apple Watch, and AirPods', 85.00, 129.99, 40, 10, 'UNIT', 'ACTIVE', 1, 5, NOW(), NOW()),
('Ugreen 100W GaN Fast Wall Charger', 'SKU-PROD-006', '885100000006', '4-port USB-C GaN fast desktop charger', 45.00, 74.99, 120, 20, 'PCS', 'ACTIVE', 1, 1, NOW(), NOW()),
('Amazon Echo Dot 5th Gen', 'SKU-PROD-007', '885100000007', 'Smart speaker with Alexa and vibrant sound', 28.00, 49.99, 65, 15, 'UNIT', 'ACTIVE', 1, 2, NOW(), NOW()),
('Xiaomi Smart Air Purifier 4', 'SKU-PROD-008', '885100000008', 'High-efficiency filtration for clean, allergen-free air', 110.00, 169.99, 18, 5, 'UNIT', 'ACTIVE', 1, 3, NOW(), NOW()),
('Philips Hue White & Color Ambiance Bulb', 'SKU-PROD-009', '885100000009', 'Smart LED bulb compatible with Zigbee & Bluetooth', 30.00, 49.99, 90, 15, 'BOX', 'ACTIVE', 1, 4, NOW(), NOW()),
('TP-Link Kasa Smart Wi-Fi Plug Mini', 'SKU-PROD-010', '885100000010', 'Compact smart plug with energy monitoring and timer', 12.00, 19.99, 150, 25, 'BOX', 'ACTIVE', 1, 5, NOW(), NOW()),

-- Computers & Laptops (Category 2)
('Apple MacBook Air 13-inch M3', 'SKU-PROD-011', '885100000011', 'Liquid Retina display, 8-core CPU, 10-core GPU, 8GB RAM', 850.00, 1099.00, 35, 8, 'UNIT', 'ACTIVE', 2, 1, NOW(), NOW()),
('Apple MacBook Pro 16-inch M3 Max', 'SKU-PROD-012', '885100000012', '36GB unified memory, 1TB SSD, Liquid Retina XDR', 2800.00, 3499.00, 12, 3, 'UNIT', 'ACTIVE', 2, 2, NOW(), NOW()),
('Dell XPS 15 9530 Laptop', 'SKU-PROD-013', '885100000013', 'Intel Core i9-13900H, RTX 4070, 32GB RAM, 1TB SSD', 1950.00, 2499.00, 15, 4, 'UNIT', 'ACTIVE', 2, 3, NOW(), NOW()),
('Lenovo ThinkPad X1 Carbon Gen 11', 'SKU-PROD-014', '885100000014', 'Intel Core i7-1365U, 16GB RAM, 512GB SSD, ultralight', 1250.00, 1649.00, 22, 5, 'UNIT', 'ACTIVE', 2, 4, NOW(), NOW()),
('HP Spectre x360 14 2-in-1', 'SKU-PROD-015', '885100000015', 'Intel Core Ultra 7, OLED touchscreen, 16GB RAM', 1100.00, 1449.00, 18, 5, 'UNIT', 'ACTIVE', 2, 5, NOW(), NOW()),
('Asus Zenbook 14 OLED', 'SKU-PROD-016', '885100000016', 'Intel Core Ultra 7 155H, 16GB RAM, 1TB SSD, 3K OLED 120Hz', 850.00, 1099.00, 28, 6, 'UNIT', 'ACTIVE', 2, 1, NOW(), NOW()),
('Microsoft Surface Laptop 5 13.5-inch', 'SKU-PROD-017', '885100000017', 'Intel Core i7, 16GB RAM, 512GB SSD, touchscreen', 1050.00, 1399.00, 14, 4, 'UNIT', 'ACTIVE', 2, 2, NOW(), NOW()),
('Apple Mac Mini M2 Pro', 'SKU-PROD-018', '885100000018', '10-core CPU, 16-core GPU, 16GB RAM, 512GB SSD', 1000.00, 1299.00, 20, 5, 'UNIT', 'ACTIVE', 2, 3, NOW(), NOW()),
('Dell OptiPlex 7010 Micro Desktop', 'SKU-PROD-019', '885100000019', 'Intel Core i5-13500T, 16GB RAM, 512GB NVMe SSD', 550.00, 749.00, 30, 8, 'UNIT', 'ACTIVE', 2, 4, NOW(), NOW()),
('Lenovo IdeaCentre 5i Tower Desktop', 'SKU-PROD-020', '885100000020', 'Intel Core i7-13700, 16GB RAM, 1TB SSD, Intel UHD 770', 680.00, 899.00, 16, 4, 'UNIT', 'ACTIVE', 2, 5, NOW(), NOW()),

-- Computer Accessories (Category 3)
('Logitech MX Master 3S Wireless Mouse', 'SKU-PROD-021', '885100000021', '8K DPI sensor, Quiet clicks, MagSpeed electromagnetic scroll', 65.00, 99.99, 85, 15, 'PCS', 'ACTIVE', 3, 1, NOW(), NOW()),
('Logitech MX Mechanical Wireless Keyboard', 'SKU-PROD-022', '885100000022', 'Low-profile mechanical switches, tactile quiet, backlit', 110.00, 169.99, 50, 10, 'PCS', 'ACTIVE', 3, 2, NOW(), NOW()),
('Dell UltraSharp U2723QE 27 4K Monitor', 'SKU-PROD-023', '885100000023', 'IPS Black technology, USB-C hub with 90W power delivery', 420.00, 579.99, 20, 5, 'UNIT', 'ACTIVE', 3, 3, NOW(), NOW()),
('LG 34WN80C-B 34 Curved UltraWide Monitor', 'SKU-PROD-024', '885100000024', 'WQHD 3440x1440 IPS display with USB Type-C 60W', 390.00, 549.99, 15, 4, 'UNIT', 'ACTIVE', 3, 4, NOW(), NOW()),
('Anker 575 USB-C Docking Station (13-in-1)', 'SKU-PROD-025', '885100000025', 'Triple display support with 85W high-speed laptop charging', 140.00, 199.99, 40, 8, 'UNIT', 'ACTIVE', 3, 5, NOW(), NOW()),
('Logitech Brio 4K Ultra HD Webcam', 'SKU-PROD-026', '885100000026', 'HDR, RightLight 3, dual omni-directional noise-canceling mics', 120.00, 169.99, 60, 12, 'PCS', 'ACTIVE', 3, 1, NOW(), NOW()),
('CalDigit TS4 Thunderbolt 4 Dock', 'SKU-PROD-027', '885100000027', '18 ports of connectivity with 98W host power delivery', 310.00, 399.95, 18, 5, 'UNIT', 'ACTIVE', 3, 2, NOW(), NOW()),
('Keychron Q1 Pro Wireless Custom Keyboard', 'SKU-PROD-028', '885100000028', 'QMK/VIA wireless mechanical keyboard, CNC aluminum frame', 140.00, 199.00, 32, 6, 'PCS', 'ACTIVE', 3, 3, NOW(), NOW()),
('Apple Magic Trackpad - Black', 'SKU-PROD-029', '885100000029', 'Wireless, rechargeable, Multi-Touch gestures and Force Touch', 105.00, 149.00, 45, 10, 'PCS', 'ACTIVE', 3, 4, NOW(), NOW()),
('SteelSeries QcK Heavy XXL Gaming Mouse Pad', 'SKU-PROD-030', '885100000030', 'Extra thick non-slip rubber base, micro-woven cloth', 18.00, 29.99, 110, 20, 'PCS', 'ACTIVE', 3, 5, NOW(), NOW()),

-- Audio & Sound (Category 4)
('Sony WH-1000XM5 Wireless Noise Canceling Headphones', 'SKU-PROD-031', '885100000031', 'Industry leading active noise cancellation, 30h battery', 260.00, 399.99, 42, 8, 'PCS', 'ACTIVE', 4, 1, NOW(), NOW()),
('Bose QuietComfort Ultra Headphones', 'SKU-PROD-032', '885100000032', 'Spatial audio, world-class noise cancellation, custom sound', 280.00, 429.00, 30, 6, 'PCS', 'ACTIVE', 4, 2, NOW(), NOW()),
('Apple AirPods Pro 2nd Gen USB-C', 'SKU-PROD-033', '885100000033', 'Active noise cancellation, Adaptive Audio, MagSafe USB-C case', 180.00, 249.00, 75, 15, 'PCS', 'ACTIVE', 4, 3, NOW(), NOW()),
('Sennheiser Momentum 4 Wireless', 'SKU-PROD-034', '885100000034', 'Audiophile-inspired acoustics, 60-hour battery life', 220.00, 349.95, 25, 5, 'PCS', 'ACTIVE', 4, 4, NOW(), NOW()),
('Shure SM7B Vocal Dynamic Microphone', 'SKU-PROD-035', '885100000035', 'Legendary dynamic cardioid studio vocal microphone', 310.00, 399.00, 20, 4, 'UNIT', 'ACTIVE', 4, 5, NOW(), NOW()),
('Rode Wireless PRO Dual Microphone System', 'SKU-PROD-036', '885100000036', '32-bit float on-board recording, timecode sync, Lavalier mics', 320.00, 399.00, 15, 3, 'SET', 'ACTIVE', 4, 1, NOW(), NOW()),
('JBL Flip 6 Portable Bluetooth Speaker', 'SKU-PROD-037', '885100000037', 'IP67 waterproof and dustproof, 12 hours playtime', 75.00, 129.95, 95, 18, 'PCS', 'ACTIVE', 4, 2, NOW(), NOW()),
('Sonos Beam Gen 2 Compact Smart Soundbar', 'SKU-PROD-038', '885100000038', 'Dolby Atmos panoramic sound, crystal clear dialogue', 340.00, 499.00, 14, 3, 'UNIT', 'ACTIVE', 4, 3, NOW(), NOW()),
('Audio-Technica ATH-M50x Studio Monitor Headphones', 'SKU-PROD-039', '885100000039', 'Exceptional clarity, extended frequency range, deep bass', 95.00, 149.00, 60, 12, 'PCS', 'ACTIVE', 4, 4, NOW(), NOW()),
('Blue Yeti USB Microphone - Blackout', 'SKU-PROD-040', '885100000040', 'Custom three-capsule array, 4 pickup patterns, plug and play', 80.00, 129.99, 55, 10, 'PCS', 'ACTIVE', 4, 5, NOW(), NOW()),

-- Gaming Gear (Category 5)
('Sony PlayStation 5 Slim Console', 'SKU-PROD-041', '885100000041', '1TB SSD storage, ultra-high speed, ray tracing support', 410.00, 499.99, 28, 5, 'UNIT', 'ACTIVE', 5, 1, NOW(), NOW()),
('Microsoft Xbox Series X Console', 'SKU-PROD-042', '885100000042', '12 teraflops raw graphic processing power, 1TB SSD', 410.00, 499.99, 20, 5, 'UNIT', 'ACTIVE', 5, 2, NOW(), NOW()),
('Nintendo Switch OLED Model - White', 'SKU-PROD-043', '885100000043', '7-inch vibrant OLED screen, wide adjustable stand, 64GB', 280.00, 349.99, 38, 8, 'UNIT', 'ACTIVE', 5, 3, NOW(), NOW()),
('Razer DeathAdder V3 Pro Wireless', 'SKU-PROD-044', '885100000044', '63g ultra-lightweight esports ergonomic wireless mouse', 95.00, 149.99, 65, 12, 'PCS', 'ACTIVE', 5, 4, NOW(), NOW()),
('Razer Huntsman V3 Pro Mechanical Keyboard', 'SKU-PROD-045', '885100000045', 'Analog optical switches, rapid trigger, aluminum top plate', 170.00, 249.99, 30, 6, 'PCS', 'ACTIVE', 5, 5, NOW(), NOW()),
('HyperX Cloud III Wireless Gaming Headset', 'SKU-PROD-046', '885100000046', 'Up to 120-hour battery, 53mm angled drivers, ultra comfort', 110.00, 169.99, 45, 10, 'PCS', 'ACTIVE', 5, 1, NOW(), NOW()),
('Sony DualSense Edge Wireless Controller', 'SKU-PROD-047', '885100000047', 'High-performance customizable PS5 gamepad with back buttons', 150.00, 199.99, 32, 6, 'PCS', 'ACTIVE', 5, 2, NOW(), NOW()),
('Xbox Elite Wireless Controller Series 2', 'SKU-PROD-048', '885100000048', 'Adjustable tension thumbsticks, wrap-around rubberized grip', 130.00, 179.99, 25, 5, 'PCS', 'ACTIVE', 5, 3, NOW(), NOW()),
('Secretlab TITAN Evo Gaming Chair - Stealth', 'SKU-PROD-049', '885100000049', 'Ergonomic magnetic memory foam head pillow, 4-way lumbar', 410.00, 549.00, 8, 2, 'UNIT', 'ACTIVE', 5, 4, NOW(), NOW()),
('Elgato Stream Deck MK.2', 'SKU-PROD-050', '885100000050', '15 customizable LCD keys for livestreaming and macro actions', 100.00, 149.99, 50, 10, 'PCS', 'ACTIVE', 5, 5, NOW(), NOW()),

-- Office Supplies (Category 6)
('HP LaserJet Pro MFP 3101fdw Printer', 'SKU-PROD-051', '885100000051', 'Wireless multifunction monochrome laser printer with fax', 190.00, 259.99, 15, 3, 'UNIT', 'ACTIVE', 6, 1, NOW(), NOW()),
('Epson EcoTank ET-2850 All-in-One Cartridge-Free', 'SKU-PROD-052', '885100000052', 'High-capacity ink tanks with auto 2-sided printing', 210.00, 299.99, 18, 4, 'UNIT', 'ACTIVE', 6, 2, NOW(), NOW()),
('Fellowes Powershred 79Ci Cross-Cut Paper Shredder', 'SKU-PROD-053', '885100000053', '100% jam proof system, 16 sheet shredding capacity', 160.00, 229.99, 12, 3, 'UNIT', 'ACTIVE', 6, 3, NOW(), NOW()),
('Brother P-touch Cube Plus Label Maker', 'SKU-PROD-054', '885100000054', 'Bluetooth wireless label maker with rechargeable Li-ion battery', 70.00, 99.99, 40, 8, 'UNIT', 'ACTIVE', 6, 4, NOW(), NOW()),
('Hammermill Copy Plus Paper 20lb Letter Case', 'SKU-PROD-055', '885100000055', '8.5 x 11, 500 sheets/ream, 10 reams case (5000 sheets)', 38.00, 54.99, 150, 30, 'BOX', 'ACTIVE', 6, 5, NOW(), NOW()),
('Pilot G2 Premium Gel Ink Pens 0.7mm Black (12-Pack)', 'SKU-PROD-056', '885100000056', 'Smooth-writing, longest-lasting gel ink pens', 9.50, 15.99, 220, 40, 'BOX', 'ACTIVE', 6, 1, NOW(), NOW()),
('Post-it Super Sticky Notes 3x3 Canary Yellow (12-Pack)', 'SKU-PROD-057', '885100000057', 'Twice the sticking power, holds stronger and lasts longer', 12.00, 19.99, 180, 30, 'PACK', 'ACTIVE', 6, 2, NOW(), NOW()),
('Scotch Heavy Duty Packaging Tape (6-Pack)', 'SKU-PROD-058', '885100000058', 'Strong solvent-free adhesive holds down up to 80 lbs', 14.00, 22.99, 130, 25, 'PACK', 'ACTIVE', 6, 3, NOW(), NOW()),
('Bostitch Office Heavy Duty Stapler 40-Sheet', 'SKU-PROD-059', '885100000059', 'No-jam technology, spring-powered stapler with 5000 staples', 15.00, 24.99, 85, 15, 'PCS', 'ACTIVE', 6, 4, NOW(), NOW()),
('Deflecto Sustainable Ergonomic Desk Organizer', 'SKU-PROD-060', '885100000060', 'Multi-compartment storage caddy for desktop office tools', 11.00, 18.99, 90, 15, 'PCS', 'ACTIVE', 6, 5, NOW(), NOW()),

-- Networking Equipment (Category 7)
('Ubiquiti UniFi Dream Machine Special Edition', 'SKU-PROD-061', '885100000061', 'Enterprise-grade all-in-one router, gateway, PoE switch', 410.00, 499.00, 10, 2, 'UNIT', 'ACTIVE', 7, 1, NOW(), NOW()),
('Ubiquiti UniFi U6 Pro Wi-Fi 6 Access Point', 'SKU-PROD-062', '885100000062', 'Dual-band Wi-Fi 6, 5.3 Gbps aggregate throughput, PoE', 125.00, 159.00, 35, 8, 'UNIT', 'ACTIVE', 7, 2, NOW(), NOW()),
('ASUS RT-AX88U Pro Dual-Band Wi-Fi 6 Router', 'SKU-PROD-063', '885100000063', 'Dual 2.5G ports, quad-core 2.0 GHz CPU, AiMesh support', 220.00, 299.99, 22, 5, 'UNIT', 'ACTIVE', 7, 3, NOW(), NOW()),
('TP-Link Deco XE75 Pro AXE5400 Mesh System (3-Pack)', 'SKU-PROD-064', '885100000064', 'Tri-band Wi-Fi 6E mesh coverage up to 7200 sq.ft', 280.00, 399.99, 15, 3, 'SET', 'ACTIVE', 7, 4, NOW(), NOW()),
('Netgear GS108 8-Port Gigabit Ethernet Unmanaged Switch', 'SKU-PROD-065', '885100000065', 'ProSAFE plug-and-play desktop metal housing switch', 25.00, 39.99, 110, 20, 'UNIT', 'ACTIVE', 7, 5, NOW(), NOW()),
('Cisco CBS110-16T Unmanaged 16-Port Gigabit Switch', 'SKU-PROD-066', '885100000066', '16 x 10/100/1000 ports with energy efficient ethernet', 95.00, 139.99, 24, 5, 'UNIT', 'ACTIVE', 7, 1, NOW(), NOW()),
('Cat 6 Ethernet Patch Cable 50ft Snagless', 'SKU-PROD-067', '885100000067', '550MHz UTP bare copper wire RJ45 network cable', 8.00, 14.99, 160, 30, 'PCS', 'ACTIVE', 7, 2, NOW(), NOW()),
('Synology DiskStation DS224+ 2-Bay NAS', 'SKU-PROD-068', '885100000068', 'Intel Celeron J4125 4-core, 2GB DDR4 expandable to 6GB', 250.00, 319.99, 16, 4, 'UNIT', 'ACTIVE', 7, 3, NOW(), NOW()),
('MikroTik hEX RB750Gr3 5-Port Gigabit Router', 'SKU-PROD-069', '885100000069', 'Dual core 880MHz CPU, 256MB RAM, RouterOS L4 license', 42.00, 59.95, 45, 10, 'UNIT', 'ACTIVE', 7, 4, NOW(), NOW()),
('Netgear Nighthawk M6 Pro 5G Mobile Hotspot Router', 'SKU-PROD-070', '885100000070', 'Wi-Fi 6E, mmWave 5G connectivity, up to 8 Gbps speeds', 680.00, 899.99, 8, 2, 'UNIT', 'ACTIVE', 7, 5, NOW(), NOW()),

-- Wearable Technology (Category 8)
('Apple Watch Ultra 2 GPS + Cellular 49mm', 'SKU-PROD-071', '885100000071', 'Rugged titanium case, precision dual-frequency GPS, 3000 nits', 650.00, 799.00, 18, 4, 'PCS', 'ACTIVE', 8, 1, NOW(), NOW()),
('Apple Watch Series 9 GPS 45mm', 'SKU-PROD-072', '885100000072', 'S9 SiP, Double tap gesture, brighter display, ECG sensor', 340.00, 429.00, 35, 8, 'PCS', 'ACTIVE', 8, 2, NOW(), NOW()),
('Samsung Galaxy Watch 6 Classic 47mm', 'SKU-PROD-073', '885100000073', 'Rotating bezel, advanced sleep coaching, BIA body analysis', 290.00, 399.99, 28, 6, 'PCS', 'ACTIVE', 8, 3, NOW(), NOW()),
('Garmin Fenix 7 Pro Solar Multisport GPS Watch', 'SKU-PROD-074', '885100000074', 'Solar charging lens, built-in LED flashlight, endurance score', 620.00, 799.99, 14, 3, 'PCS', 'ACTIVE', 8, 4, NOW(), NOW()),
('Fitbit Charge 6 Fitness Tracker', 'SKU-PROD-075', '885100000075', 'Built-in GPS, heart rate on gym equipment, 40+ exercise modes', 105.00, 159.95, 55, 12, 'PCS', 'ACTIVE', 8, 5, NOW(), NOW()),
('Meta Quest 3 128GB VR Headset', 'SKU-PROD-076', '885100000076', 'Mixed reality, 4K+ Infinite Display, Touch Plus controllers', 410.00, 499.99, 22, 5, 'UNIT', 'ACTIVE', 8, 1, NOW(), NOW()),
('Oura Ring Gen 3 Horizon - Stealth Black', 'SKU-PROD-077', '885100000077', 'Smart ring health tracker for sleep, readiness, and heart rate', 290.00, 399.00, 20, 4, 'PCS', 'ACTIVE', 8, 2, NOW(), NOW()),
('WHOOP 4.0 Health & Fitness Tracker Band', 'SKU-PROD-078', '885100000078', '24/7 continuous health monitoring with strain & recovery metrics', 190.00, 239.00, 30, 6, 'PCS', 'ACTIVE', 8, 3, NOW(), NOW()),
('Garmin Forerunner 265 Running Smartwatch', 'SKU-PROD-079', '885100000079', 'AMOLED touchscreen display, training readiness, HRV status', 340.00, 449.99, 16, 3, 'PCS', 'ACTIVE', 8, 4, NOW(), NOW()),
('Ray-Ban Meta Wayfarer Smart Glasses', 'SKU-PROD-080', '885100000080', '12 MP camera, open-ear audio, Meta AI assistant integration', 230.00, 299.00, 25, 5, 'PCS', 'ACTIVE', 8, 5, NOW(), NOW()),

-- Cameras & Photography (Category 9)
('Sony Alpha a7 IV Full-Frame Mirrorless Camera', 'SKU-PROD-081', '885100000081', '33MP Exmor R sensor, 4K 60p, real-time eye AF for photo/video', 2000.00, 2498.00, 8, 2, 'UNIT', 'ACTIVE', 9, 1, NOW(), NOW()),
('Canon EOS R6 Mark II Mirrorless Camera Body', 'SKU-PROD-082', '885100000082', '24.2MP full frame, 40 fps electronic shutter, 4K 60p 6K oversampling', 1950.00, 2399.00, 7, 2, 'UNIT', 'ACTIVE', 9, 2, NOW(), NOW()),
('Fujifilm X-T5 Mirrorless Camera Body - Black', 'SKU-PROD-083', '885100000083', '40.2MP X-Trans CMOS 5 HR sensor, 5-axis in-body stabilization', 1350.00, 1699.95, 10, 2, 'UNIT', 'ACTIVE', 9, 3, NOW(), NOW()),
('Sony FE 24-70mm f/2.8 GM II Lens', 'SKU-PROD-084', '885100000084', 'G Master standard zoom lens, fast f/2.8 constant aperture', 1850.00, 2298.00, 6, 2, 'PCS', 'ACTIVE', 9, 4, NOW(), NOW()),
('Canon RF 50mm f/1.8 STM Lens', 'SKU-PROD-085', '885100000085', 'Compact, lightweight normal prime lens for EOS R cameras', 140.00, 199.00, 45, 10, 'PCS', 'ACTIVE', 9, 5, NOW(), NOW()),
('DJI Mini 4 Pro Drone with RC 2 Controller', 'SKU-PROD-086', '885100000086', 'Under 249g, 4K 60fps HDR video, omnidirectional obstacle sensing', 620.00, 759.00, 15, 3, 'UNIT', 'ACTIVE', 9, 1, NOW(), NOW()),
('GoPro HERO12 Black Action Camera', 'SKU-PROD-087', '885100000087', '5.3K 60 video, HyperSmooth 6.0 stabilization, waterproof to 33ft', 280.00, 399.99, 40, 8, 'PCS', 'ACTIVE', 9, 2, NOW(), NOW()),
('DJI Osmo Pocket 3 Gimbal Camera', 'SKU-PROD-088', '885100000088', '1-inch CMOS pocket-sized camera with 4K 120fps and 2-inch rotatable OLED', 420.00, 519.00, 22, 5, 'UNIT', 'ACTIVE', 9, 3, NOW(), NOW()),
('Peak Design Everyday Backpack 20L v2', 'SKU-PROD-089', '885100000089', 'Versatile camera and laptop backpack with weatherproof zippers', 200.00, 279.95, 25, 5, 'PCS', 'ACTIVE', 9, 4, NOW(), NOW()),
('Manfrotto Befree Advanced Carbon Fiber Tripod', 'SKU-PROD-090', '885100000090', 'Lightweight travel tripod with ball head, 19.8 lb payload', 260.00, 349.88, 14, 3, 'PCS', 'ACTIVE', 9, 5, NOW(), NOW()),

-- Storage & Memory (Category 10)
('Samsung 990 PRO 2TB PCIe 4.0 M.2 NVMe SSD', 'SKU-PROD-091', '885100000091', 'Up to 7,450 MB/s sequential read, heatsink model', 140.00, 199.99, 70, 15, 'PCS', 'ACTIVE', 10, 1, NOW(), NOW()),
('Samsung 990 PRO 4TB PCIe 4.0 M.2 NVMe SSD', 'SKU-PROD-092', '885100000092', 'High-capacity lightning fast NVMe SSD for gamers & creators', 260.00, 349.99, 35, 8, 'PCS', 'ACTIVE', 10, 2, NOW(), NOW()),
('Crucial T700 2TB Gen5 NVMe M.2 SSD', 'SKU-PROD-093', '885100000093', 'PCIe 5.0 speed up to 12,400 MB/s read, premium aluminum heatsink', 220.00, 299.99, 20, 5, 'PCS', 'ACTIVE', 10, 3, NOW(), NOW()),
('SanDisk 2TB Extreme Portable SSD', 'SKU-PROD-094', '885100000094', 'Up to 1050 MB/s read, IP65 water and dust resistance', 110.00, 159.99, 85, 18, 'PCS', 'ACTIVE', 10, 4, NOW(), NOW()),
('Samsung T7 Shield 4TB Portable SSD', 'SKU-PROD-095', '885100000095', 'Rugged external solid state drive, USB 3.2 Gen 2, drop resistant', 230.00, 319.99, 30, 6, 'PCS', 'ACTIVE', 10, 5, NOW(), NOW()),
('Western Digital 12TB Elements Desktop Hard Drive', 'SKU-PROD-096', '885100000096', 'High-capacity plug-and-play USB 3.0 desktop storage', 180.00, 249.99, 25, 5, 'UNIT', 'ACTIVE', 10, 1, NOW(), NOW()),
('Seagate IronWolf Pro 16TB NAS Hard Drive', 'SKU-PROD-097', '885100000097', '7200 RPM, 256MB cache, CMR for multi-bay RAID systems', 260.00, 329.99, 18, 4, 'PCS', 'ACTIVE', 10, 2, NOW(), NOW()),
('Corsair Vengeance DDR5 32GB (2x16GB) 6000MHz', 'SKU-PROD-098', '885100000098', 'Intel XMP 3.0 optimized high performance desktop RAM kit', 85.00, 119.99, 60, 12, 'KIT', 'ACTIVE', 10, 3, NOW(), NOW()),
('G.SKILL Trident Z5 RGB 64GB (2x32GB) DDR5 6400MHz', 'SKU-PROD-099', '885100000099', 'Ultra-fast dual-channel DDR5 kit with customizable RGB lighting', 160.00, 229.99, 28, 6, 'KIT', 'ACTIVE', 10, 4, NOW(), NOW()),
('SanDisk Extreme PRO 256GB SDXC UHS-I Card', 'SKU-PROD-100', '885100000100', 'Up to 200 MB/s read speed, 4K UHD video recording V30', 30.00, 48.99, 140, 25, 'PCS', 'ACTIVE', 10, 5, NOW(), NOW())
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);
