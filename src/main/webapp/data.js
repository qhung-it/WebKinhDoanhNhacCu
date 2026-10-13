/* ============================================================
   Soundora - Dữ liệu dùng chung (sản phẩm, thương hiệu, tin tức)
   Sau này bạn có thể thay file này bằng dữ liệu lấy từ API/Backend.
   ============================================================ */

const SITE = {
  name: 'Soundora',
  freeShipFrom: 500000,
  shipFee: 30000,
  perPage: 12
};

const CATEGORIES = [
  { key: 'guitar',   name: 'Guitar',    icon: '🎸' },
  { key: 'piano',    name: 'Piano',     icon: '🎹' },
  { key: 'trong',    name: 'Trống',     icon: '🥁' },
  { key: 'organ',    name: 'Organ',     icon: '🎼' },
  { key: 'ukulele',  name: 'Ukulele',   icon: '🪕' },
  { key: 'violin',   name: 'Violin',    icon: '🎻' },
  { key: 'ken-sao',  name: 'Kèn & Sáo', icon: '🎷' },
  { key: 'phu-kien', name: 'Phụ kiện',  icon: '🎚️' }
];

const BRANDS = [
  { name: 'Yamaha',    origin: 'Nhật Bản', desc: 'Thương hiệu nhạc cụ hàng đầu thế giới, nổi tiếng với piano, guitar và organ.' },
  { name: 'Fender',    origin: 'Mỹ',       desc: 'Cái nôi của Stratocaster và Telecaster huyền thoại.' },
  { name: 'Ibanez',    origin: 'Nhật Bản', desc: 'Chuyên guitar điện, bass với cần đàn mỏng, tốc độ cao.' },
  { name: 'Taylor',    origin: 'Mỹ',       desc: 'Guitar acoustic cao cấp với âm thanh trong trẻo, tinh tế.' },
  { name: 'Roland',    origin: 'Nhật Bản', desc: 'Dẫn đầu về piano điện, trống điện và synthesizer.' },
  { name: 'Cort',      origin: 'Hàn Quốc', desc: 'Guitar chất lượng tốt, giá hợp lý cho người mới bắt đầu.' },
  { name: 'Casio',     origin: 'Nhật Bản', desc: 'Organ và piano điện gọn nhẹ, dễ sử dụng, giá dễ tiếp cận.' },
  { name: 'Gibson',    origin: 'Mỹ',       desc: 'Les Paul, SG, ES - những cây đàn đã làm nên lịch sử rock.' },
  { name: 'Marshall',  origin: 'Anh',      desc: 'Ampli và loa mang âm thanh rock đặc trưng.' },
  { name: 'Boss',      origin: 'Nhật Bản', desc: 'Pedal hiệu ứng và thiết bị guitar bền bỉ, chuyên nghiệp.' },
  { name: 'Pearl',     origin: 'Nhật Bản', desc: 'Bộ trống cơ chất lượng cao cho cả học viên lẫn nghệ sĩ.' },
  { name: 'Kala',      origin: 'Mỹ',       desc: 'Ukulele thiết kế đẹp, âm vang ấm áp.' },
  { name: 'Stentor',   origin: 'Anh',      desc: 'Violin học sinh và sinh viên được nhiều trường nhạc tin dùng.' },
  { name: 'Dunlop',    origin: 'Mỹ',       desc: 'Phím gảy, capo và phụ kiện guitar nổi tiếng.' },
  { name: "D'Addario", origin: 'Mỹ',       desc: 'Dây đàn chất lượng số 1 cho guitar, bass, violin.' },
  { name: 'Zildjian',  origin: 'Mỹ',       desc: 'Thương hiệu cymbal lâu đời hơn 400 năm.' }
];

const CAT_INFO = {
  guitar: {
    features: ['Thiết kế chuẩn form, dễ chơi cho cả người mới lẫn người đã có kinh nghiệm', 'Cần đàn được làm phẳng, action thấp, hạn chế đau ngón tay'],
    specs: [['Loại nhạc cụ', 'Guitar'], ['Số phím', '20 - 22 phím'], ['Dây đàn', '6 dây tiêu chuẩn'], ['Cần đàn', 'Gỗ tuyển chọn, xử lý chống cong vênh'], ['Phụ kiện kèm theo', 'Phím gảy, dây dự phòng, cờ lê chỉnh cần']]
  },
  piano: {
    features: ['Phím bấm cảm ứng lực mô phỏng đàn piano cơ', 'Âm thanh sampling chân thực, hỗ trợ cổng tai nghe và kết nối USB'],
    specs: [['Loại nhạc cụ', 'Piano điện'], ['Số phím', '88 phím'], ['Độ đa âm', '64 - 256 notes'], ['Kết nối', 'USB, tai nghe, Pedal'], ['Phụ kiện kèm theo', 'Adapter nguồn, pedal, giá nhạc']]
  },
  trong: {
    features: ['Cấu hình đầy đủ, sẵn sàng chơi ngay sau khi lắp đặt', 'Chất liệu bền bỉ, âm thanh vang và rõ'],
    specs: [['Loại nhạc cụ', 'Trống / Cymbal'], ['Chất liệu', 'Gỗ Poplar / Birch / Mesh'], ['Cấu hình', 'Theo bộ tiêu chuẩn của hãng'], ['Màu sắc', 'Theo từng phiên bản'], ['Phụ kiện kèm theo', 'Dùi trống, ghế, chân pedal']]
  },
  organ: {
    features: ['Hàng trăm âm sắc và điệu nhạc có sẵn', 'Phù hợp học tập, luyện tập tại nhà và biểu diễn nhỏ'],
    specs: [['Loại nhạc cụ', 'Organ điện tử'], ['Số phím', '61 phím'], ['Âm sắc', '400+ âm sắc'], ['Điệu nhạc', '100+ điệu nhạc'], ['Phụ kiện kèm theo', 'Adapter, giá nhạc']]
  },
  ukulele: {
    features: ['Kích thước nhỏ gọn, dễ mang theo khi đi dã ngoại', 'Âm thanh vui tươi, dễ học cho người mới'],
    specs: [['Loại nhạc cụ', 'Ukulele'], ['Kích thước', 'Soprano / Concert'], ['Dây đàn', 'Nylon 4 dây'], ['Mặt đàn', 'Gỗ tuyển chọn'], ['Phụ kiện kèm theo', 'Bao đàn, dây dự phòng']]
  },
  violin: {
    features: ['Gỗ được sấy kỹ, âm thanh sáng và đầy đặn', 'Bộ phụ kiện đầy đủ, sẵn sàng tập ngay'],
    specs: [['Loại nhạc cụ', 'Violin'], ['Kích thước', '4/4'], ['Mặt đàn', 'Gỗ vân sam (Spruce)'], ['Lưng và cần', 'Gỗ thích (Maple)'], ['Phụ kiện kèm theo', 'Hộp đàn, vĩ, nhựa thông']]
  },
  'ken-sao': {
    features: ['Chất liệu bền, dễ vệ sinh và bảo quản', 'Phù hợp cho học sinh, sinh viên và người chơi nghiệp dư'],
    specs: [['Loại nhạc cụ', 'Kèn / Sáo'], ['Chất liệu', 'Nhựa ABS / Đồng thau'], ['Âm vực', 'Theo chuẩn của hãng'], ['Bảo hành', '12 tháng'], ['Phụ kiện kèm theo', 'Hộp đựng, dầu bôi trơn, khăn lau']]
  },
  'phu-kien': {
    features: ['Phụ kiện chính hãng, độ bền cao', 'Tương thích với đa số nhạc cụ cùng loại'],
    specs: [['Loại sản phẩm', 'Phụ kiện / Thiết bị âm thanh'], ['Xuất xứ', 'Theo hãng sản xuất'], ['Tình trạng', 'Mới 100%, nguyên seal'], ['Bảo hành', '6 - 12 tháng'], ['Đóng gói', 'Hộp chính hãng']]
  }
};

/* id, name, cat, brand, emoji, price, old, rating, reviews, badge, sold, isNew, highlight */
function _p(id, name, cat, brand, emoji, price, old, rating, reviews, badge, sold, isNew, hl) {
  const c = CATEGORIES.find(x => x.key === cat);
  const info = CAT_INFO[cat];
  return {
    id, name, cat, catName: c.name, brand, emoji, price, old, rating, reviews, badge, sold, isNew,
    sku: brand.substring(0, 3).toUpperCase() + '-' + String(1000 + id),
    stock: 5 + (id * 7) % 30,
    short: hl,
    desc: name + ' là lựa chọn nổi bật của ' + brand + ' trong nhóm ' + c.name.toLowerCase() + '. ' + hl +
      ' Sản phẩm được Soundora nhập khẩu chính hãng, kiểm tra kỹ trước khi giao, bảo hành đầy đủ và hỗ trợ đổi trả trong 30 ngày.',
    features: [hl].concat(info.features, ['Cam kết 100% chính hãng ' + brand + ', bảo hành 12 tháng, đổi mới trong 30 ngày']),
    specs: [['Thương hiệu', brand], ['Mã sản phẩm (SKU)', brand.substring(0, 3).toUpperCase() + '-' + String(1000 + id)]].concat(info.specs)
  };
}

const PRODUCTS = [
  _p(1,  'Yamaha F310 - Guitar Acoustic',              'guitar',   'Yamaha',    '🎸', 3490000,  3990000,  4.9, 128, 'hot',  980, false, 'Mặt đàn gỗ vân sam cho âm sáng, ngân vang chuẩn xác, bán chạy số 1 cho người mới.'),
  _p(2,  'Fender Player Stratocaster',                 'guitar',   'Fender',    '🎸', 17900000, null,     4.8, 64,  null,  210, false, 'Huyền thoại Stratocaster với 3 single-coil cho âm thanh trong và cá tính.'),
  _p(3,  'Ibanez GRG121DX - Guitar Điện',              'guitar',   'Ibanez',    '🎸', 6290000,  6990000,  4.7, 52,  'sale', 340, false, 'Cần đàn mỏng, chơi solo nhanh, phù hợp rock và metal.'),
  _p(4,  'Taylor GS Mini Mahogany',                    'guitar',   'Taylor',    '🎸', 19500000, null,     4.9, 31,  null,  95,  false, 'Dáng đàn nhỏ gọn nhưng âm lượng lớn, thích hợp du lịch và thu âm.'),
  _p(5,  'Cort AD810 - Guitar Acoustic',               'guitar',   'Cort',      '🎸', 2390000,  2790000,  4.6, 210, 'hot',  1250, false, 'Mức giá dễ tiếp cận nhưng chất lượng ổn định, âm thanh ấm áp.'),
  _p(6,  'Yamaha P-125 - Piano Điện',                  'piano',    'Yamaha',    '🎹', 16900000, 19900000, 4.9, 96,  'sale', 410, false, 'Phím GHS cảm ứng lực, âm thanh sampling từ đàn grand piano CFIIIS.'),
  _p(7,  'Roland FP-30X - Piano Điện',                 'piano',    'Roland',    '🎹', 18500000, null,     4.8, 47,  null,  180, false, 'Động cơ âm thanh SuperNATURAL, kết nối Bluetooth MIDI.'),
  _p(8,  'Casio CT-S300 - Organ',                      'organ',    'Casio',     '🎹', 3590000,  3990000,  4.5, 143, null,  760, false, 'Gọn nhẹ, nhiều điệu nhạc, phù hợp tập luyện tại nhà.'),
  _p(9,  'Casio CDP-S160 - Piano Điện Mỏng',           'piano',    'Casio',     '🎹', 9890000,  10990000, 4.7, 58,  'new',  120, true,  'Thiết kế siêu mỏng, bàn phím phản hồi lực Smart Scaled.'),
  _p(10, 'Yamaha PSR-E373 - Organ',                    'organ',    'Yamaha',    '🎹', 5990000,  6790000,  4.8, 175, 'hot',  890, false, '622 âm sắc, 205 điệu nhạc, có chế độ học nhạc thông minh.'),
  _p(11, 'Roland TD-1DMK - Trống Điện',                'trong',    'Roland',    '🥁', 15900000, 17900000, 4.7, 29,  null,  75,  false, 'Tập trống yên tĩnh trong căn hộ, mặt mesh cho cảm giác thật.'),
  _p(12, 'Pearl Roadshow 5 Piece - Trống Cơ',          'trong',    'Pearl',     '🥁', 12900000, 14500000, 4.6, 22,  'sale', 60,  false, 'Bộ trống 5 món đầy đủ cymbal, hardware và ghế ngồi.'),
  _p(13, 'Zildjian S Family Cymbal Pack',              'trong',    'Zildjian',  '🥁', 7900000,  8900000,  4.7, 25,  null,  48,  false, 'Bộ cymbal 3 món cho âm thanh sáng, rõ và bền.'),
  _p(14, 'Kala KA-15S - Ukulele Soprano',              'ukulele',  'Kala',      '🪕', 1290000,  1590000,  4.8, 301, 'hot',  1600, false, 'Gỗ Mahogany, âm ấm, đẹp mắt, bán chạy nhất dòng ukulele.'),
  _p(15, 'Cort UKE Concert',                           'ukulele',  'Cort',      '🪕', 1090000,  null,     4.5, 89,  null,  430, false, 'Size Concert dễ bấm hợp tay người lớn, giá tốt.'),
  _p(16, 'Yamaha V3SKA - Violin 4/4',                  'violin',   'Yamaha',    '🎻', 6890000,  7590000,  4.8, 37,  'new',  70,  true,  'Violin học viên cao cấp, thùng đàn gỗ thích cho âm sáng.'),
  _p(17, 'Stentor Student I - Violin 4/4',             'violin',   'Stentor',   '🎻', 3290000,  3790000,  4.6, 66,  null,  210, false, 'Chất lượng ổn định, được nhiều trường nhạc lựa chọn.'),
  _p(18, 'Yamaha YRS-24B - Sáo Recorder',              'ken-sao',  'Yamaha',    '🪈', 190000,   250000,   4.9, 520, 'hot',  3800, false, 'Sáo recorder chuẩn trường học, âm chuẩn, dễ thổi.'),
  _p(19, 'Yamaha YAS-280 - Saxophone Alto',            'ken-sao',  'Yamaha',    '🎷', 24900000, 27900000, 4.8, 14,  null,  22,  false, 'Alto Sax cho người mới, phím nhẹ, dễ ra âm.'),
  _p(20, 'Yamaha YTR-2330 - Trumpet',                  'ken-sao',  'Yamaha',    '🎺', 14500000, null,     4.7, 12,  'new',  18,  true,  'Trumpet Bb cho học viên, thân đồng thau mạ vàng.'),
  _p(21, 'Marshall MG15 - Ampli Guitar',               'phu-kien', 'Marshall',  '🔊', 3190000,  3590000,  4.7, 88,  'sale', 520, false, 'Ampli 15W, 2 kênh, âm thanh rock kinh điển.'),
  _p(22, 'Boss TU-3 - Pedal Chỉnh Dây',                'phu-kien', 'Boss',      '🎚️', 2390000,  2690000,  4.8, 120, null,  640, false, 'Tuner dạng pedal chính xác, màn hình sáng rõ trên sân khấu.'),
  _p(23, "D'Addario EXL110 - Dây Guitar Điện",         'phu-kien', "D'Addario", '🎼', 190000,   250000,   4.9, 860, 'hot',  5400, false, 'Bộ dây điện 10-46 chuẩn quốc tế, bền và sáng tiếng.'),
  _p(24, 'Dunlop Tortex - Phím Gảy (12 cái)',          'phu-kien', 'Dunlop',    '🎵', 69000,    90000,    4.9, 1020, null, 6100, false, 'Chất liệu Tortex bền, bám tay, nhiều độ dày.'),
  _p(25, 'Gibson Les Paul Studio',                     'guitar',   'Gibson',    '🎸', 52900000, null,     4.9, 9,   'new',  6,   true,  'Biểu tượng rock với humbucker mạnh mẽ, sustain dài.'),
  _p(26, 'Marshall Stanmore II - Loa Bluetooth',       'phu-kien', 'Marshall',  '🔊', 9990000,  10990000, 4.7, 41,  null,  130, false, 'Loa Bluetooth thiết kế ampli cổ điển, âm thanh đa chiều.'),
  _p(27, 'Boss Katana-50 MkII - Ampli',                'phu-kien', 'Boss',      '🔊', 7990000,  8990000,  4.8, 55,  'sale', 160, false, 'Ampli 50W với nhiều hiệu ứng tích hợp, kết nối USB thu âm.')
];

const COUPONS = {
  SOUNDORA10: { type: 'PERCENTAGE', value: 10, max: 500000, min: 0,       desc: 'Giảm 10% (tối đa 500.000đ)' },
  FREESHIP:   { type: 'ship',    value: 0,  max: 0,      min: 0,       desc: 'Miễn phí vận chuyển' },
  WELCOME50:  { type: 'FIXED_AMOUNT',  value: 50000, max: 0,   min: 1000000, desc: 'Giảm 50.000đ cho đơn từ 1.000.000đ' }
};

const NEWS = [
  {
    id: 1, cat: 'Hướng dẫn', emoji: '🎸', color: '#6C5CE7', color2: '#A29BFE', date: '22/09/2026', author: 'Minh Hoàng',
    title: 'Hướng dẫn chọn Guitar Acoustic cho người mới bắt đầu',
    excerpt: 'Tìm hiểu các yếu tố quan trọng khi chọn mua cây đàn guitar acoustic đầu tiên: dáng đàn, loại gỗ mặt top, độ cao action và ngân sách phù hợp.',
    content: [
      'Chọn cây guitar đầu tiên là bước quan trọng quyết định bạn có giữ được niềm hứng thú khi tập hay không. Một cây đàn quá cứng tay hoặc phô ra âm thanh khó nghe sẽ khiến người mới nhanh nản.',
      'Hãy ưu tiên dáng đàn Dreadnought hoặc Concert tùy kích thước cơ thể, mặt top gỗ Spruce cho âm sáng, và action thấp (khoảng 2-3mm tại phím 12) để bấm dễ dàng. Ngân sách hợp lý cho người mới thường từ 2 đến 4 triệu đồng.',
      'Cuối cùng, đừng quên kiểm tra phụ kiện đi kèm như bao đàn, dây dự phòng, phím gảy và cờ lê chỉnh cần. Tại Soundora, đội ngũ tư vấn luôn sẵn sàng giúp bạn thử đàn trực tiếp trước khi quyết định.'
    ]
  },
  {
    id: 2, cat: 'Review', emoji: '🎹', color: '#0984E3', color2: '#74B9FF', date: '15/09/2026', author: 'Thanh Tâm',
    title: 'Review Piano điện Yamaha P-125: Đáng tiền trong tầm giá?',
    excerpt: 'Đánh giá chi tiết về cảm giác phím, chất lượng âm thanh, tính năng và độ bền của Yamaha P-125 sau hai tháng sử dụng thực tế.',
    content: [
      'Yamaha P-125 là một trong những cây piano điện bán chạy nhất thế giới nhờ sự cân bằng giữa chất lượng và giá thành. Thân máy gọn, loa tích hợp đủ lớn để tập luyện ở phòng nhỏ.',
      'Bàn phím GHS có độ nặng giảm dần từ trầm lên cao giống piano cơ, cho cảm giác chơi tự nhiên. Âm thanh được lấy mẫu từ grand piano concert nên khá đầy đặn, nhất là khi dùng tai nghe.',
      'Điểm trừ nhỏ là số lượng âm sắc không nhiều và không có Bluetooth. Tuy vậy, nếu bạn cần một cây đàn học nghiêm túc và bền bỉ thì P-125 vẫn là lựa chọn rất đáng cân nhắc.'
    ]
  },
  {
    id: 3, cat: 'Tin tức', emoji: '🎵', color: '#00B894', color2: '#55EFC4', date: '10/09/2026', author: 'Quốc Bảo',
    title: 'Soundora khai trương cửa hàng mới tại Quận 7 với nhiều ưu đãi',
    excerpt: 'Chào mừng cửa hàng thứ 5 trong hệ thống với không gian trải nghiệm nhạc cụ rộng hơn 300m2 và hàng loạt quà tặng cho khách hàng.',
    content: [
      'Nhằm phục vụ khách hàng khu vực phía Nam thành phố, Soundora chính thức khai trương cửa hàng mới tại Quận 7 với không gian rộng hơn 300m2, chia thành các khu trải nghiệm guitar, piano, trống và phụ kiện.',
      'Trong tuần khai trương, khách hàng sẽ được giảm đến 20% cho các dòng sản phẩm chọn lọc, tặng kèm bao đàn và khóa học thử miễn phí với giảng viên.',
      'Cửa hàng mở cửa từ 8:00 đến 21:00 tất cả các ngày trong tuần. Hãy ghé thử đàn và cảm nhận âm thanh trực tiếp bạn nhé.'
    ]
  },
  {
    id: 4, cat: 'Mẹo hay', emoji: '🎼', color: '#FDCB6E', color2: '#FFEAA7', date: '05/09/2026', author: 'Minh Hoàng',
    title: '7 mẹo bảo quản đàn guitar trong mùa mưa ẩm',
    excerpt: 'Khí hậu nồm ẩm có thể làm cong cần đàn, nở mặt gỗ và gỉ dây. Cùng xem những cách đơn giản để giữ đàn luôn ở trạng thái tốt nhất.',
    content: [
      'Độ ẩm cao là kẻ thù số một của nhạc cụ gỗ. Gỗ hút ẩm sẽ nở ra, làm thay đổi độ cong cần đàn, khiến action cao và gây ra tiếng rè.',
      'Hãy cất đàn trong bao hoặc hộp cứng kèm gói hút ẩm silica gel, tránh để sát tường hoặc gần cửa sổ. Thường xuyên lau dây bằng khăn mềm sau khi chơi và thay dây định kỳ 2-3 tháng.',
      'Nếu có điều kiện, hãy dùng máy hút ẩm hoặc điều hòa duy trì độ ẩm 40-55% trong phòng đặt đàn. Đây là cách bảo vệ cây đàn của bạn hiệu quả nhất.'
    ]
  },
  {
    id: 5, cat: 'Sự kiện', emoji: '🥁', color: '#FF6B6B', color2: '#FFA8A8', date: '28/08/2026', author: 'Thanh Tâm',
    title: 'Soundora Music Day 2026: Đêm nhạc cùng học viên và nghệ sĩ khách mời',
    excerpt: 'Sự kiện thường niên quy tụ hơn 500 người yêu nhạc với các tiết mục biểu diễn, workshop và nhiều phần quà hấp dẫn.',
    content: [
      'Soundora Music Day trở lại với chủ đề "Play Your Passion", nơi học viên, giảng viên và các nghệ sĩ khách mời cùng tham gia biểu diễn trên sân khấu ngoài trời.',
      'Bên cạnh phần biểu diễn, chương trình còn có các workshop ngắn về kỹ thuật fingerstyle, nhập môn trống điện và cách thu âm tại nhà với chi phí thấp.',
      'Vé tham dự miễn phí cho khách hàng đã mua sắm tại Soundora trong năm 2026. Đừng quên mang theo hóa đơn hoặc số điện thoại đã đăng ký khi tới sự kiện.'
    ]
  },
  {
    id: 6, cat: 'Hướng dẫn', emoji: '🎻', color: '#6C5CE7', color2: '#A29BFE', date: '20/08/2026', author: 'Quốc Bảo',
    title: 'Chọn Violin cho người mới: kích thước, chất liệu và phụ kiện',
    excerpt: 'Violin có nhiều kích cỡ từ 1/16 đến 4/4. Chọn đúng kích thước giúp bạn tránh đau vai, đau cổ khi tập và tiến bộ nhanh hơn.',
    content: [
      'Kích thước violin phụ thuộc vào chiều dài cánh tay của người chơi. Người lớn thường dùng size 4/4, trẻ em cần chọn size nhỏ hơn và thay đổi khi lớn lên.',
      'Mặt đàn nên bằng gỗ Spruce, lưng và cần đàn bằng gỗ Maple để có âm sáng. Đừng quên bộ phụ kiện như vĩ, nhựa thông, kê vai và hộp đàn.',
      'Bạn nên thử cầm đàn trước khi mua để cảm nhận độ vừa tay. Đội ngũ Soundora sẽ hỗ trợ đo kích thước và chỉnh dây miễn phí.'
    ]
  },
  {
    id: 7, cat: 'Review', emoji: '🔊', color: '#0984E3', color2: '#74B9FF', date: '12/08/2026', author: 'Thanh Tâm',
    title: 'Marshall MG15: Ampli nhỏ gọn cho người mới tập guitar điện',
    excerpt: 'Với công suất 15W và 2 kênh âm thanh, MG15 là lựa chọn đáng giá để tập luyện tại nhà mà vẫn giữ được chất rock.',
    content: [
      'Marshall MG15 nổi bật với thiết kế cổ điển và âm thanh đặc trưng của Marshall. Công suất 15W đủ để chơi trong phòng và tập cùng ban nhạc nhỏ.',
      'Kênh Clean cho âm thanh sạch và sáng, kênh Overdrive đủ gắt để chơi rock/blues. Núm chỉnh EQ 3 band dễ dùng, cổng tai nghe thuận tiện khi tập đêm.',
      'Tóm lại, đây là một chiếc ampli có giá hợp lý, bền bỉ và là bước khởi đầu tuyệt vời cho người mới chơi guitar điện.'
    ]
  },
  {
    id: 8, cat: 'Mẹo hay', emoji: '🎚️', color: '#FDCB6E', color2: '#FFEAA7', date: '02/08/2026', author: 'Minh Hoàng',
    title: 'Cách lên dây guitar chính xác chỉ trong 1 phút',
    excerpt: 'Chỉ cần một chiếc tuner kẹp hoặc ứng dụng điện thoại, bạn đã có thể lên dây guitar chuẩn mà không cần tai nghe nhạc.',
    content: [
      'Thứ tự dây guitar chuẩn từ dây 6 đến dây 1 là E - A - D - G - B - E. Bạn nên bắt đầu từ dây dày nhất và chỉnh từng dây một.',
      'Với tuner kẹp, hãy gảy từng dây nhẹ nhàng và vặn khóa cho đến khi kim hoặc màn hình báo xanh. Nếu dây bị thấp, hãy vặn lên từ từ để tránh đứt.',
      'Sau khi lên đủ 6 dây, hãy kiểm tra lại một lượt vì dây mới sẽ giãn và bị lệch. Thói quen này sẽ giúp tai nghe của bạn nhạy hơn qua thời gian.'
    ]
  }
];

const INFO_PAGES = {
  about: {
    title: 'Giới thiệu về Soundora',
    icon: 'fa-music',
    body: [
      'Soundora là hệ thống cửa hàng nhạc cụ chính hãng với hơn 10 năm kinh nghiệm, phục vụ hàng trăm nghìn khách hàng trên toàn quốc.',
      'Chúng tôi phân phối nhạc cụ và phụ kiện từ các thương hiệu hàng đầu thế giới như Yamaha, Fender, Roland, Taylor, Casio... với cam kết 100% chính hãng.',
      'Sứ mệnh của Soundora là đưa âm nhạc đến gần hơn với mọi người, từ người mới bắt đầu cho đến nghệ sĩ chuyên nghiệp, thông qua sản phẩm chất lượng và dịch vụ tận tâm.'
    ]
  },
  store: {
    title: 'Hệ thống cửa hàng',
    icon: 'fa-map-marker-alt',
    body: [
      'Trụ sở chính: 123 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh.',
      'Chi nhánh Quận 7: 45 Nguyễn Thị Thập, Quận 7, TP. Hồ Chí Minh.',
      'Chi nhánh Hà Nội: 88 Tây Sơn, Đống Đa, Hà Nội.',
      'Giờ mở cửa: 8:00 - 21:00 tất cả các ngày trong tuần. Hotline: 1900 9999.'
    ]
  },
  careers: {
    title: 'Tuyển dụng',
    icon: 'fa-briefcase',
    body: [
      'Soundora luôn chào đón những người yêu âm nhạc và có tinh thần phục vụ khách hàng.',
      'Các vị trí đang tuyển: Nhân viên tư vấn bán hàng, Kỹ thuật viên nhạc cụ, Chuyên viên marketing, Nhân viên kho vận.',
      'Vui lòng gửi CV về email tuyendung@soundora.vn hoặc liên hệ hotline 1900 9999 để biết thêm chi tiết.'
    ]
  },
  warranty: {
    title: 'Chính sách bảo hành',
    icon: 'fa-shield-alt',
    body: [
      'Tất cả sản phẩm tại Soundora đều được bảo hành chính hãng từ 6 đến 12 tháng tùy loại.',
      'Bảo hành áp dụng cho lỗi do nhà sản xuất. Không bảo hành đối với hư hỏng do va đập, vào nước, tự ý sửa chữa hoặc sử dụng sai cách.',
      'Để được bảo hành, vui lòng mang sản phẩm và hóa đơn đến cửa hàng hoặc liên hệ hotline để được hướng dẫn gửi sản phẩm.'
    ]
  },
  return: {
    title: 'Chính sách đổi trả',
    icon: 'fa-undo-alt',
    body: [
      'Đổi trả miễn phí trong vòng 30 ngày nếu sản phẩm lỗi do nhà sản xuất.',
      'Sản phẩm đổi trả cần còn nguyên tem, hộp và đầy đủ phụ kiện đi kèm.',
      'Hoàn tiền trong vòng 3-7 ngày làm việc sau khi chúng tôi nhận và kiểm tra sản phẩm.'
    ]
  },
  guide: {
    title: 'Hướng dẫn mua hàng',
    icon: 'fa-shopping-cart',
    body: [
      'Bước 1: Tìm sản phẩm bằng thanh tìm kiếm hoặc duyệt theo danh mục.',
      'Bước 2: Nhấn "Thêm vào giỏ" và kiểm tra lại giỏ hàng của bạn.',
      'Bước 3: Nhập mã giảm giá (nếu có), chọn "Thanh toán" và điền thông tin nhận hàng.',
      'Bước 4: Chọn phương thức thanh toán và xác nhận đơn hàng. Chúng tôi sẽ liên hệ xác nhận trong thời gian sớm nhất.'
    ]
  },
  faq: {
    title: 'Câu hỏi thường gặp',
    icon: 'fa-question-circle',
    body: [
      'Hỏi: Sản phẩm có chính hãng không? Đáp: 100% sản phẩm chính hãng, có đầy đủ tem và giấy tờ.',
      'Hỏi: Thời gian giao hàng bao lâu? Đáp: Nội thành 1-2 ngày, toàn quốc 2-5 ngày.',
      'Hỏi: Tôi có thể thử đàn trước khi mua không? Đáp: Có, bạn có thể đến cửa hàng để thử trực tiếp.',
      'Hỏi: Có hỗ trợ trả góp không? Đáp: Có, hỗ trợ trả góp 0% qua thẻ tín dụng cho đơn từ 3.000.000đ.'
    ]
  }
};

/* ============================================================
   [NÂNG CẤP] Lớp dữ liệu dùng chung giữa Cửa hàng và Admin
   - Admin lưu thay đổi vào localStorage (khóa sdr_a_*), phần này nạp chồng lên dữ liệu gốc.
   - Khi có backend thật, chỉ cần thay phần này bằng lời gọi API.
   ============================================================ */
const SDR = {
  slug(s) { return String(s).toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/đ/g, 'd').replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, ''); },
  hash(s) { let h = 5381; for (let i = 0; i < s.length; i++) h = ((h << 5) + h + s.charCodeAt(i)) | 0; return String(h); },
  read(k, d) { try { const v = localStorage.getItem('sdr_' + k); return v ? JSON.parse(v) : d; } catch (e) { return d; } },
  write(k, v) { try { localStorage.setItem('sdr_' + k, JSON.stringify(v)); } catch (e) { /* bỏ qua */ } }
};

/* Hạng thành viên mặc định (Admin có thể chỉnh) */
const DEFAULT_TIERS = [
  { key: 'thuong',     name: 'Thường',     min: 0,        discount: 0, color: '#8E8E93' },
  { key: 'thanh-vien', name: 'Thành viên', min: 5000000,  discount: 2, color: '#0984E3' },
  { key: 'vip',        name: 'VIP',        min: 20000000, discount: 5, color: '#E1A200' }
];
const TIERS = DEFAULT_TIERS.slice();

(function applyOverrides() {
  const replaceArr = (arr, next) => { arr.length = 0; next.forEach(x => arr.push(x)); };
  const cats = SDR.read('a_categories', null);   if (Array.isArray(cats)) replaceArr(CATEGORIES, cats);
  const brands = SDR.read('a_brands', null);     if (Array.isArray(brands)) replaceArr(BRANDS, brands);
  const prods = SDR.read('a_products', null);    if (Array.isArray(prods)) replaceArr(PRODUCTS, prods);
  const news = SDR.read('a_news', null);         if (Array.isArray(news)) replaceArr(NEWS, news);
  const coupons = SDR.read('a_coupons', null);
  if (coupons && typeof coupons === 'object') { Object.keys(COUPONS).forEach(k => delete COUPONS[k]); Object.assign(COUPONS, coupons); }
  const tiers = SDR.read('a_tiers', null);       if (Array.isArray(tiers) && tiers.length) replaceArr(TIERS, tiers);
  const site = SDR.read('a_site', null);         if (site) Object.assign(SITE, site);
  const stock = SDR.read('a_stock', {});          // { id: tồn kho hiện tại } do đơn hàng / nhập kho thay đổi
  PRODUCTS.forEach(p => { if (stock[p.id] != null) p.stock = stock[p.id]; });
  // Danh mục đang tắt => ẩn sản phẩm thuộc danh mục đó trên cửa hàng
  const hiddenCats = CATEGORIES.filter(c => c.active === false).map(c => c.key);
  window.ALL_PRODUCTS = PRODUCTS.slice();        // bản đầy đủ cho Admin
  const isAdmin = document.body && document.body.dataset.page === 'admin';
  if (!isAdmin) {
    const visible = PRODUCTS.filter(p => p.active !== false && !hiddenCats.includes(p.cat));
    replaceArr(PRODUCTS, visible);
    const vis = CATEGORIES.filter(c => c.active !== false);
    replaceArr(CATEGORIES, vis);
    const now = Date.now();
    Object.keys(COUPONS).forEach(k => {
      const c = COUPONS[k];
      if (c.active === false || (c.end && new Date(c.end).getTime() + 86399000 < now) || (c.start && new Date(c.start).getTime() > now)) delete COUPONS[k];
    });
  }
})();

/* ---------- Ảnh sản phẩm ----------
   - Nếu sản phẩm có p.img (đường dẫn ảnh thật, ví dụ 'images/products/yamaha-f310.jpg') => dùng ảnh đó.
   - Nếu chưa có / ảnh lỗi => tự vẽ ảnh minh họa SVG theo loại nhạc cụ + màu thương hiệu.
   => Mọi trang (danh sách, chi tiết, giỏ hàng, thanh toán, đơn hàng) đều dùng chung 1 hàm productImg(). */
const ART_COLORS = {
  guitar: ['#6C5CE7', '#A29BFE'], piano: ['#2D3436', '#636E72'], trong: ['#D63031', '#FF7675'], organ: ['#0984E3', '#74B9FF'],
  ukulele: ['#E17055', '#FAB1A0'], violin: ['#B33771', '#FD7272'], 'ken-sao': ['#F39C12', '#FDCB6E'], 'phu-kien': ['#00B894', '#55EFC4']
};
function artShape(cat) {
  const w = '#fff', s = 'rgba(0,0,0,.18)';
  switch (cat) {
    case 'guitar': return '<g transform="rotate(-35 200 200)"><rect x="188" y="30" width="24" height="170" rx="6" fill="' + w + '" opacity=".95"/><rect x="180" y="12" width="40" height="40" rx="8" fill="' + w + '"/><ellipse cx="200" cy="262" rx="62" ry="58" fill="' + w + '"/><ellipse cx="200" cy="205" rx="44" ry="40" fill="' + w + '"/><circle cx="200" cy="240" r="20" fill="' + s + '"/><rect x="170" y="296" width="60" height="9" rx="4" fill="' + s + '"/></g>';
    case 'piano': return '<rect x="50" y="130" width="300" height="150" rx="14" fill="' + w + '"/>' + [0,1,2,3,4,5,6].map(i => '<line x1="' + (93 + i * 43) + '" y1="130" x2="' + (93 + i * 43) + '" y2="280" stroke="' + s + '" stroke-width="2"/>').join('') + [0,1,3,4,5].map(i => '<rect x="' + (78 + i * 43) + '" y="130" width="28" height="90" rx="4" fill="#2D3436"/>').join('') + '<rect x="50" y="108" width="300" height="26" rx="8" fill="' + w + '" opacity=".6"/>';
    case 'trong': return '<ellipse cx="200" cy="250" rx="82" ry="26" fill="' + w + '"/><rect x="118" y="170" width="164" height="80" fill="' + w + '" opacity=".9"/><ellipse cx="200" cy="170" rx="82" ry="26" fill="' + w + '"/><ellipse cx="200" cy="170" rx="70" ry="19" fill="' + s + '"/><line x1="130" y1="92" x2="188" y2="152" stroke="' + w + '" stroke-width="9" stroke-linecap="round"/><line x1="270" y1="92" x2="212" y2="152" stroke="' + w + '" stroke-width="9" stroke-linecap="round"/>';
    case 'organ': return '<rect x="40" y="140" width="320" height="120" rx="12" fill="' + w + '"/><rect x="58" y="154" width="140" height="26" rx="5" fill="' + s + '"/>' + [0,1,2,3,4,5,6,7,8,9].map(i => '<rect x="' + (58 + i * 30) + '" y="196" width="24" height="50" rx="3" fill="#2D3436" opacity=".85"/>').join('') + '<circle cx="300" cy="167" r="9" fill="' + s + '"/><circle cx="328" cy="167" r="9" fill="' + s + '"/>';
    case 'ukulele': return '<g transform="rotate(-30 200 200)"><rect x="192" y="52" width="16" height="130" rx="5" fill="' + w + '" opacity=".95"/><rect x="184" y="34" width="32" height="30" rx="7" fill="' + w + '"/><ellipse cx="200" cy="232" rx="50" ry="46" fill="' + w + '"/><ellipse cx="200" cy="184" rx="34" ry="30" fill="' + w + '"/><circle cx="200" cy="216" r="15" fill="' + s + '"/></g>';
    case 'violin': return '<g transform="rotate(-18 200 200)"><rect x="193" y="40" width="14" height="120" rx="5" fill="' + w + '" opacity=".95"/><circle cx="200" cy="34" r="14" fill="' + w + '"/><path d="M200 140 C150 140 140 190 165 205 C140 225 145 285 200 285 C255 285 260 225 235 205 C260 190 250 140 200 140Z" fill="' + w + '"/><path d="M180 218 q8 -10 0 -20 M220 218 q-8 -10 0 -20" stroke="' + s + '" stroke-width="5" fill="none" stroke-linecap="round"/><rect x="186" y="262" width="28" height="6" rx="3" fill="' + s + '"/></g><line x1="90" y1="300" x2="320" y2="120" stroke="' + w + '" stroke-width="5" stroke-linecap="round" opacity=".8"/>';
    case 'ken-sao': return '<g transform="rotate(-20 200 200)"><rect x="150" y="190" width="200" height="22" rx="11" fill="' + w + '"/>' + [0,1,2,3,4].map(i => '<circle cx="' + (180 + i * 30) + '" cy="201" r="6" fill="' + s + '"/>').join('') + '<path d="M150 190 h-30 a40 40 0 0 0 0 22 h30z" fill="' + w + '"/><path d="M350 190 l30 -20 l0 62 l-30 -20z" fill="' + w + '" opacity=".9"/></g>';
    default: return '<rect x="110" y="110" width="180" height="190" rx="16" fill="' + w + '"/><circle cx="200" cy="225" r="52" fill="' + s + '"/><circle cx="200" cy="225" r="30" fill="' + w + '" opacity=".85"/><circle cx="200" cy="225" r="9" fill="' + s + '"/><circle cx="145" cy="140" r="9" fill="' + s + '"/><circle cx="255" cy="140" r="9" fill="' + s + '"/>';
  }
}
function svgArt(p) {
  const c = ART_COLORS[p.cat] || ['#6C5CE7', '#A29BFE'];
  const label = String(p.brand || '').replace(/[&<>"']/g, '');
  const svg = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 400"><defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="' + c[0] + '"/><stop offset="1" stop-color="' + c[1] + '"/></linearGradient><radialGradient id="r" cx=".3" cy=".25" r=".8"><stop offset="0" stop-color="#fff" stop-opacity=".35"/><stop offset="1" stop-color="#fff" stop-opacity="0"/></radialGradient></defs><rect width="400" height="400" fill="url(#g)"/><rect width="400" height="400" fill="url(#r)"/><circle cx="340" cy="60" r="90" fill="#fff" opacity=".08"/><circle cx="50" cy="360" r="110" fill="#fff" opacity=".07"/>' + artShape(p.cat) + '<text x="200" y="372" text-anchor="middle" font-family="Poppins,Arial,sans-serif" font-size="22" font-weight="700" letter-spacing="3" fill="#fff" opacity=".9">' + label.toUpperCase() + '</text></svg>';
  return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svg);
}
/* URL ảnh chính của sản phẩm (ưu tiên ảnh thật p.img) */
function productImg(p, i) {
  if (!p) return '';
  const list = Array.isArray(p.images) && p.images.length ? p.images : (p.img ? [p.img] : []);
  return list[i || 0] || svgArt(p);
}
/* Thẻ <img> dùng chung: có lazy-load + tự quay về ảnh minh họa nếu ảnh thật bị lỗi */
function imgTag(p, cls, i) {
  const fallback = svgArt(p);
  return '<img class="' + (cls || 'pimg') + '" src="' + productImg(p, i).replace(/"/g, '&quot;') + '" alt="' + String(p.name || '').replace(/"/g, '&quot;') + '" loading="lazy" decoding="async" onerror="this.onerror=null;this.src=\'' + fallback.replace(/'/g, '%27') + '\'">';
}


/* ============================================================
   [CLASS DIAGRAM] Enum + lược đồ dữ liệu theo báo cáo UML
   User(role, customerRank) · Order(status, payment, deposit) · Payment · Deposit · Promotion
   ============================================================ */
const ORDER_STATUS   = { PENDING: 'Chờ xử lý', COMPLETED: 'Hoàn thành', FAILED: 'Thất bại', CANCELLED: 'Đã hủy' };
const PAYMENT_METHOD = { CASH: 'Tiền mặt (COD)', E_WALLET: 'Ví điện tử (MoMo / ZaloPay)' };
const PAYMENT_STATUS = { PENDING: 'Chờ thanh toán', COMPLETED: 'Đã thanh toán', FAILED: 'Thất bại', REFUNDED: 'Đã hoàn tiền' };
const DEPOSIT_STATUS = { PENDING: 'Chờ xác nhận cọc', COMPLETED: 'Đã đặt cọc', REFUNDED: 'Đã hoàn cọc' };
const DISCOUNT_TYPE  = { PERCENTAGE: 'Giảm theo %', FIXED_AMOUNT: 'Giảm số tiền' };
const ROLE_NAMES     = { customer: 'Khách hàng', admin: 'Quản trị viên', manager: 'Quản lý bán hàng', staff: 'Nhân viên', content: 'Biên tập nội dung' };
const DEPOSIT = { rate: 0.3, from: 5000000 };    // đơn từ 5 triệu được chọn đặt cọc 30%

/* Tính tiền cọc của đơn (Deposit.calculateDeposit) */
function calculateDeposit(total) { return total >= DEPOSIT.from ? Math.round(total * DEPOSIT.rate / 1000) * 1000 : 0; }

/* Migrate dữ liệu cũ -> lược đồ mới (chạy 1 lần) */
(function migrateSchema() {
  if (SDR.read('schema', 0) >= 2) return;
  const stMap = { 'Đang xử lý': 'PENDING', 'Đã xác nhận': 'PENDING', 'Đang giao': 'PENDING', 'Hoàn thành': 'COMPLETED', 'Đã hủy': 'CANCELLED' };
  const psMap = { 'Chưa thanh toán': 'PENDING', 'Đã thanh toán': 'COMPLETED', 'Đã hoàn tiền': 'REFUNDED' };
  const orders = SDR.read('orders', []);
  orders.forEach(o => {
    o.status = stMap[o.status] || (ORDER_STATUS[o.status] ? o.status : 'PENDING');
    if (!o.payment) o.payment = { paymentId: 'PAY' + String(o.code || '').slice(3), paymentMethod: o.pay === 'momo' ? 'E_WALLET' : 'CASH', amount: o.totals ? o.totals.total : 0, paymentDate: o.createdAt || null, paymentStatus: psMap[o.payStatus] || 'PENDING' };
    if (o.deposit === undefined) o.deposit = null;
    o.orderId = o.orderId || o.code; o.depositAmount = o.depositAmount || 0;
    if (o.totals) { o.subtotal = o.totals.sub; o.discount = (o.totals.discount || 0) + (o.totals.tierDiscount || 0); o.totalAmount = o.totals.total; }
    o.shippingAddress = o.shippingAddress || (o.customer ? o.customer.addr : '');
    (o.history || []).forEach(h => { h.status = stMap[h.status] || h.status; });
    delete o.pay; delete o.payStatus;
  });
  SDR.write('orders', orders);
  const cps = SDR.read('a_coupons', null);
  if (cps) { Object.keys(cps).forEach(k => { if (cps[k].type === 'percent') cps[k].type = 'PERCENTAGE'; if (cps[k].type === 'amount') cps[k].type = 'FIXED_AMOUNT'; }); SDR.write('a_coupons', cps); }
  // Người dùng: thêm userId + role; gộp tài khoản quản trị cũ (a_staff) vào cùng bảng User
  const users = SDR.read('users', []);
  users.forEach((u, i) => { u.role = u.role || 'customer'; u.userId = u.userId || ('U' + String(Date.now() + i).slice(-8)); if (u.locked == null) u.locked = false; });
  (SDR.read('a_staff', []) || []).forEach(s => { if (!users.some(u => u.email === s.email)) users.push({ userId: 'S' + String(Date.now()).slice(-7) + users.length, name: s.name, email: s.email, phone: '', address: '', pass: s.pass, role: s.role, locked: s.active === false, created: s.created }); });
  if (!users.some(u => u.role === 'admin')) users.push({ userId: 'ADMIN01', name: 'Quản trị viên', email: 'admin@soundora.vn', phone: '', address: '', pass: SDR.hash('admin123'), role: 'admin', locked: false, created: new Date().toISOString() });
  SDR.write('users', users);
  SDR.write('schema', 2);
})();
