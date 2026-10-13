/* ============================================================
   Soundora - Cấu hình giao diện và ảnh minh họa.
   Dữ liệu sản phẩm, tin tức, khuyến mãi và danh mục được nạp từ API.
   ============================================================ */

const SITE = {
  name: 'Soundora',
  freeShipFrom: 0,
  shipFee: 0,
  perPage: 12
};

const CATEGORIES = [];

const BRANDS = [];



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

const PRODUCTS = [];

const COUPONS = {};

const NEWS = [];

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

/* Hạng thành viên mặc định (Admin có thể chỉnh) */
const DEFAULT_TIERS = [];
const TIERS = DEFAULT_TIERS.slice();

window.ALL_PRODUCTS = PRODUCTS.slice();

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
// Sắp xếp sản phẩm theo giá tăng dần và gán ảnh tương ứng 1-27
if (typeof PRODUCTS !== 'undefined' && Array.isArray(PRODUCTS)) {
  // 1. Sắp xếp danh sách sản phẩm theo giá từ thấp đến cao trước
  PRODUCTS.sort((a, b) => a.price - b.price);

  // Danh sách vị trí các ảnh có định dạng .webp và .gif
  const webpImgNums = [12, 14, 18, 19, 20, 24, 25, 26, 27];
  const gifImgNums = [23];

  // 2. Gán ảnh tương ứng từ 1 đến 27 cho từng sản phẩm đã sắp xếp theo giá
  PRODUCTS.forEach((p, index) => {
    const imgNum = index + 1; // Số thứ tự ảnh tương ứng (từ 1 đến 27)

    if (webpImgNums.includes(imgNum)) {
      p.img = `images/${imgNum}.webp`;
    } else if (gifImgNums.includes(imgNum)) {
      p.img = `images/${imgNum}.gif`;
    } else {
      p.img = `images/${imgNum}.jpg`;
    }
  });
}