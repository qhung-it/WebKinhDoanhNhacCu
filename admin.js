/* ============================================================
   Soundora Admin - Giao diện quản trị
   Dữ liệu lưu localStorage (sdr_*). Khi có backend: thay các hàm
   load()/save() bằng lời gọi API, giao diện giữ nguyên.
   Tài khoản mặc định: admin@soundora.vn / admin123 (hãy đổi mật khẩu!)
   ============================================================ */
const $ = (s, r) => (r || document).querySelector(s);
const $$ = (s, r) => Array.from((r || document).querySelectorAll(s));
const fmt = n => Number(n || 0).toLocaleString('vi-VN') + 'đ';
const esc = s => String(s == null ? '' : s).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const hash = s => { let h = 5381; for (let i = 0; i < s.length; i++) h = ((h << 5) + h + s.charCodeAt(i)) | 0; return String(h); };
const slug = s => String(s).toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/đ/g, 'd').replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
const App = $('#adminApp');
const ROLES = {
  admin:   { name: 'Quản trị viên', pages: '*' },
  manager: { name: 'Quản lý bán hàng', pages: ['dashboard', 'products', 'categories', 'brands', 'inventory', 'orders', 'payments', 'customers', 'tiers', 'reviews', 'promotions', 'stats', 'contacts'] },
  staff:   { name: 'Nhân viên', pages: ['dashboard', 'orders', 'payments', 'customers', 'reviews', 'contacts', 'inventory'] },
  content: { name: 'Biên tập nội dung', pages: ['dashboard', 'content', 'reviews', 'contacts'] }
};
const ORDER_FLOW = ['Đang xử lý', 'Đã xác nhận', 'Đang giao', 'Hoàn thành'];
const PAY_NAMES = { cod: 'COD', bank: 'Chuyển khoản', momo: 'MoMo / ZaloPay' };

/* ---------- Dữ liệu ---------- */
const load = (k, d) => SDR.read(k, d);
const save = (k, v) => SDR.write(k, v);
const getProducts = () => load('a_products', null) || JSON.parse(JSON.stringify(ALL_PRODUCTS));
const saveProducts = l => save('a_products', l);
const getCats = () => load('a_categories', null) || JSON.parse(JSON.stringify(CATEGORIES));
const getBrands = () => load('a_brands', null) || JSON.parse(JSON.stringify(BRANDS));
const getNews = () => load('a_news', null) || JSON.parse(JSON.stringify(NEWS));
const getCoupons = () => load('a_coupons', null) || JSON.parse(JSON.stringify(COUPONS));
const getTiers = () => load('a_tiers', null) || JSON.parse(JSON.stringify(DEFAULT_TIERS));
const getOrders = () => load('orders', []);
const getUsers = () => load('users', []);
const getStaff = () => {
  let s = load('a_staff', null);
  if (!s) { s = [{ name: 'Quản trị viên', email: 'admin@soundora.vn', pass: hash('admin123'), role: 'admin', active: true, created: new Date().toISOString() }]; save('a_staff', s); }
  return s;
};
const me = () => load('a_session', null);
function log(action, detail) {
  const l = load('a_log', []); const u = me();
  l.unshift({ at: new Date().toISOString(), by: u ? u.email : '-', action, detail: detail || '' });
  save('a_log', l.slice(0, 400));
}
const can = page => { const u = me(); if (!u) return false; const r = ROLES[u.role]; return r && (r.pages === '*' || r.pages.includes(page)); };
const dt = iso => { try { return new Date(iso).toLocaleString('vi-VN'); } catch (e) { return iso || ''; } };
const tierOf = spend => { const l = getTiers().slice().sort((a, b) => a.min - b.min); let t = l[0]; l.forEach(x => { if (spend >= x.min) t = x; }); return t; };
const orderSpend = email => getOrders().filter(o => o.user === email && o.status !== 'Đã hủy').reduce((s, o) => s + (o.totals ? o.totals.total : 0), 0);
const orderIso = o => o.createdAt || (function () { const m = String(o.date || '').match(/(\d{1,2})\/(\d{1,2})\/(\d{4})/); return m ? new Date(+m[3], +m[2] - 1, +m[1]).toISOString() : new Date().toISOString(); })();

/* ---------- UI helpers ---------- */
function toast(msg, type) {
  const t = document.createElement('div'); t.className = 'toast ' + (type === 'err' ? 'err' : 'ok');
  t.innerHTML = '<i class="fas ' + (type === 'err' ? 'fa-exclamation-circle' : 'fa-check-circle') + '"></i>' + esc(msg);
  $('#adminToasts').appendChild(t); setTimeout(() => t.remove(), 3200);
}
function modal(title, body, foot, size) {
  closeModal();
  const o = document.createElement('div'); o.className = 'modal-ov'; o.id = 'modalOv';
  o.innerHTML = '<div class="modal-box ' + (size || '') + '"><div class="modal-h"><h3>' + title + '</h3><button type="button" data-close aria-label="Đóng"><i class="fas fa-times"></i></button></div><div class="modal-b">' + body + '</div>' + (foot ? '<div class="modal-f">' + foot + '</div>' : '') + '</div>';
  o.addEventListener('mousedown', e => { if (e.target === o) closeModal(); });
  o.addEventListener('click', e => { if (e.target.closest('[data-close]')) closeModal(); });
  document.body.appendChild(o); return o;
}
function closeModal() { const o = $('#modalOv'); if (o) o.remove(); }
function confirmBox(msg, onYes) {
  const o = modal('Xác nhận', '<p>' + msg + '</p>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-d" id="cfYes">Xác nhận</button>', 'sm');
  $('#cfYes', o).onclick = () => { closeModal(); onYes(); };
}
const badge = (txt, cls) => '<span class="badge b-' + cls + '">' + esc(txt) + '</span>';
const orderBadge = s => badge(s, { 'Đang xử lý': 'warn', 'Đã xác nhận': 'info', 'Đang giao': 'p', 'Hoàn thành': 'ok', 'Đã hủy': 'bad' }[s] || 'mute');
const payBadge = s => badge(s, { 'Chưa thanh toán': 'warn', 'Đã thanh toán': 'ok', 'Đã hoàn tiền': 'info' }[s] || 'mute');
const empty = (icon, text) => '<div class="empty"><i class="fas ' + icon + '"></i>' + text + '</div>';
function pager(list, page, per) { const pages = Math.max(1, Math.ceil(list.length / per)); page = Math.min(page, pages); return { items: list.slice((page - 1) * per, page * per), page, pages }; }
function pagerHTML(pg) { if (pg.pages <= 1) return ''; let h = '<div class="toolbar" style="justify-content:center;margin:16px 0 0">'; for (let i = 1; i <= pg.pages; i++) h += '<button class="btn btn-s ' + (i === pg.page ? 'btn-p' : 'btn-o') + '" data-pg="' + i + '">' + i + '</button>'; return h + '</div>'; }
function csvDownload(name, rows) {
  const csv = '\ufeff' + rows.map(r => r.map(c => '"' + String(c == null ? '' : c).replace(/"/g, '""') + '"').join(',')).join('\n');
  const a = document.createElement('a'); a.href = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' })); a.download = name; a.click();
}
function readImage(file, cb) {
  if (!file) return; if (file.size > 600 * 1024) return toast('Ảnh quá lớn (tối đa 600KB). Hãy nén ảnh hoặc dùng đường dẫn ảnh.', 'err');
  const r = new FileReader(); r.onload = () => cb(r.result); r.readAsDataURL(file);
}
function setStock(id, qty, type, note) {
  const list = getProducts(); const p = list.find(x => x.id === id); if (!p) return;
  const before = p.stock; p.stock = Math.max(0, qty); saveProducts(list);
  const m = load('a_stock', {}); m[id] = p.stock; save('a_stock', m);
  const l = load('a_invlog', []); l.unshift({ at: new Date().toISOString(), id, name: p.name, type, qty: p.stock - before, before, after: p.stock, note: note || '', by: me() ? me().email : '-' });
  save('a_invlog', l.slice(0, 500));
}

/* ---------- Khung & điều hướng ---------- */
const NAV = [
  ['Tổng quan', [['dashboard', 'fa-chart-pie', 'Dashboard'], ['stats', 'fa-chart-line', 'Thống kê']]],
  ['Bán hàng', [['orders', 'fa-receipt', 'Đơn hàng'], ['payments', 'fa-credit-card', 'Thanh toán'], ['promotions', 'fa-ticket-alt', 'Khuyến mãi']]],
  ['Hàng hóa', [['products', 'fa-guitar', 'Sản phẩm'], ['categories', 'fa-layer-group', 'Danh mục'], ['brands', 'fa-award', 'Thương hiệu'], ['inventory', 'fa-warehouse', 'Kho hàng']]],
  ['Khách hàng', [['customers', 'fa-users', 'Khách hàng'], ['tiers', 'fa-crown', 'Hạng thành viên'], ['reviews', 'fa-star', 'Đánh giá'], ['contacts', 'fa-envelope', 'Liên hệ']]],
  ['Hệ thống', [['content', 'fa-newspaper', 'Tin tức & nội dung'], ['staff', 'fa-user-shield', 'Tài khoản quản trị']]]
];
const PAGES = {};
function shell(page) {
  const u = me(); const newOrders = getOrders().filter(o => o.status === 'Đang xử lý').length; const newMsg = load('contacts', []).filter(c => (c.status || 'Mới') === 'Mới').length;
  const title = (NAV.flatMap(g => g[1]).find(x => x[0] === page) || [0, 0, 'Dashboard'])[2];
  App.innerHTML = '<div class="layout"><aside class="side" id="side"><div class="side-brand"><i class="fas fa-music"></i><div>Soundora<small>ADMIN PANEL</small></div></div><nav class="side-nav">' +
    NAV.map(g => {
      const items = g[1].filter(x => can(x[0]));
      if (!items.length) return '';
      return '<div class="side-group">' + g[0] + '</div>' + items.map(x => {
        const cnt = (x[0] === 'orders' && newOrders) ? newOrders : (x[0] === 'contacts' && newMsg) ? newMsg : 0;
        return '<a href="#/' + x[0] + '" class="' + (x[0] === page ? 'active' : '') + '"><i class="fas ' + x[1] + '"></i>' + x[2] + (cnt ? '<span class="cnt">' + cnt + '</span>' : '') + '</a>';
      }).join('');
    }).join('') +
    '</nav><div class="side-foot"><div class="avatar">' + esc(u.name.charAt(0).toUpperCase()) + '</div><div style="flex:1;min-width:0"><b>' + esc(u.name) + '</b><small>' + esc(ROLES[u.role].name) + '</small></div><button class="btn btn-o btn-s" id="logoutBtn" title="Đăng xuất"><i class="fas fa-sign-out-alt"></i></button></div></aside>' +
    '<div class="main"><header class="top"><button class="burger" id="burger"><i class="fas fa-bars"></i></button><h1>' + title + '</h1><a href="index.html" target="_blank" class="btn btn-o btn-s"><i class="fas fa-external-link-alt"></i> Xem cửa hàng</a></header><div class="content" id="view"></div></div></div>';
  $('#burger').onclick = () => $('#side').classList.toggle('open');
  $('#logoutBtn').onclick = () => { log('Đăng xuất'); save('a_session', null); route(); };
  $$('.side-nav a').forEach(a => a.onclick = () => $('#side').classList.remove('open'));
}
function route() {
  if (!me()) return loginView();
  const page = (location.hash.replace('#/', '') || 'dashboard').split('?')[0];
  const target = PAGES[page] ? page : 'dashboard';
  if (!can(target)) { shell('dashboard'); $('#view').innerHTML = '<div class="card">' + empty('fa-lock', 'Bạn không có quyền truy cập mục này.') + '</div>'; return; }
  shell(target); PAGES[target]($('#view'));
  window.scrollTo(0, 0);
}
window.addEventListener('hashchange', route);

function loginView() {
  App.innerHTML = '<div class="login-wrap"><form class="login-box" id="lgForm" novalidate><div class="side-brand" style="color:var(--ink);padding:0 0 18px"><i class="fas fa-music" style="color:#fff"></i><div>Soundora<small style="color:var(--mute)">ADMIN PANEL</small></div></div><h2>Đăng nhập quản trị</h2><p>Chỉ dành cho nhân viên Soundora</p>' +
    '<div class="fg"><label>Email</label><input class="input" id="lgEmail" type="email" autocomplete="username" placeholder="admin@soundora.vn"></div><div class="fg"><label>Mật khẩu</label><input class="input" id="lgPass" type="password" autocomplete="current-password" placeholder="••••••••"></div>' +
    '<button class="btn btn-p" style="width:100%;justify-content:center;padding:12px">Đăng nhập</button><div class="hint"><i class="fas fa-info-circle"></i> Demo: <b>admin@soundora.vn</b> / <b>admin123</b>. Dữ liệu lưu trên trình duyệt này.</div><p style="margin:16px 0 0;text-align:center"><a href="index.html" style="color:var(--p)">← Về trang cửa hàng</a></p></form></div>';
  $('#lgForm').onsubmit = e => {
    e.preventDefault();
    const em = $('#lgEmail').value.trim().toLowerCase(), pw = $('#lgPass').value;
    const s = getStaff().find(x => x.email === em && x.pass === hash(pw));
    if (!s) return toast('Sai email hoặc mật khẩu', 'err');
    if (!s.active) return toast('Tài khoản quản trị đã bị vô hiệu hóa', 'err');
    save('a_session', { name: s.name, email: s.email, role: s.role }); log('Đăng nhập'); location.hash = '#/dashboard'; route();
  };
}

/* ============================================================
   DASHBOARD
   ============================================================ */
function dayKey(iso) { const d = new Date(iso); return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0'); }
const validOrders = () => getOrders().filter(o => o.status !== 'Đã hủy');
const revenueOf = list => list.reduce((s, o) => s + (o.totals ? o.totals.total : 0), 0);
PAGES.dashboard = v => {
  const orders = getOrders(), ok = validOrders(), prods = getProducts();
  const low = prods.filter(p => p.active !== false && p.stock <= 5);
  const today = dayKey(new Date().toISOString());
  const days = []; for (let i = 6; i >= 0; i--) { const d = new Date(); d.setDate(d.getDate() - i); days.push(dayKey(d.toISOString())); }
  const rev = days.map(k => revenueOf(ok.filter(o => dayKey(orderIso(o)) === k))); const max = Math.max.apply(null, rev.concat([1]));
  const sold = {}; ok.forEach(o => o.items.forEach(i => { sold[i.id] = sold[i.id] || { name: i.name, qty: 0, rev: 0 }; sold[i.id].qty += i.qty; sold[i.id].rev += i.qty * i.price; }));
  const top = Object.values(sold).sort((a, b) => b.rev - a.rev).slice(0, 5);
  const kpi = (ic, col, label, val) => '<div class="card kpi"><div class="ic" style="background:' + col + '"><i class="fas ' + ic + '"></i></div><div><small>' + label + '</small><b>' + val + '</b></div></div>';
  const stCount = s => orders.filter(o => o.status === s).length;
  v.innerHTML = '<div class="grid g4" style="margin-bottom:18px">' +
    kpi('fa-sack-dollar', 'linear-gradient(135deg,#6C5CE7,#A29BFE)', 'Doanh thu (đơn hợp lệ)', fmt(revenueOf(ok))) +
    kpi('fa-receipt', 'linear-gradient(135deg,#0984E3,#74B9FF)', 'Tổng đơn hàng', orders.length + ' <small>(' + orders.filter(o => dayKey(orderIso(o)) === today).length + ' hôm nay)</small>') +
    kpi('fa-users', 'linear-gradient(135deg,#00B894,#55EFC4)', 'Khách hàng', getUsers().length) +
    kpi('fa-box-open', 'linear-gradient(135deg,#E17055,#FAB1A0)', 'Sắp hết / hết hàng', low.length) + '</div>' +
    '<div class="grid g21" style="margin-bottom:18px"><div class="card"><h3><i class="fas fa-chart-column"></i> Doanh thu 7 ngày gần nhất</h3><div class="bars">' +
    rev.map((r, i) => '<div class="bar"><em>' + (r ? (r >= 1e6 ? (r / 1e6).toFixed(1) + 'tr' : Math.round(r / 1e3) + 'k') : '') + '</em><i style="height:' + Math.max(2, r / max * 100) + '%"></i><span>' + days[i].slice(8) + '/' + days[i].slice(5, 7) + '</span></div>').join('') + '</div></div>' +
    '<div class="card"><h3><i class="fas fa-list-check"></i> Trạng thái đơn</h3>' + ORDER_FLOW.concat(['Đã hủy']).map(s => '<div class="hbar"><span class="nm">' + s + '</span><div class="tr"><i style="width:' + (orders.length ? stCount(s) / orders.length * 100 : 0) + '%"></i></div><b>' + stCount(s) + '</b></div>').join('') + '</div></div>' +
    '<div class="grid g2"><div class="card"><h3><i class="fas fa-trophy"></i> Top sản phẩm theo doanh thu</h3>' + (top.length ? top.map(t => '<div class="hbar"><span class="nm" title="' + esc(t.name) + '">' + esc(t.name) + '</span><div class="tr"><i style="width:' + t.rev / top[0].rev * 100 + '%"></i></div><b>' + fmt(t.rev) + '</b></div>').join('') : empty('fa-chart-simple', 'Chưa có đơn hàng nào')) + '</div>' +
    '<div class="card"><h3><i class="fas fa-triangle-exclamation"></i> Cảnh báo tồn kho</h3>' + (low.length ? '<table class="tbl">' + low.slice(0, 8).map(p => '<tr><td><div class="cell"><div class="thumb">' + imgTag(p) + '</div><div><b>' + esc(p.name) + '</b><small>' + esc(p.sku) + '</small></div></div></td><td class="num">' + (p.stock ? badge('Còn ' + p.stock, 'warn') : badge('Hết hàng', 'bad')) + '</td></tr>').join('') + '</table>' : empty('fa-check-circle', 'Tồn kho ổn định')) + '</div></div>';
};

/* ============================================================
   THỐNG KÊ
   ============================================================ */
PAGES.stats = v => {
  let mode = 'day', from = '', to = '';
  const draw = () => {
    const ok = validOrders().filter(o => { const k = dayKey(orderIso(o)); return (!from || k >= from) && (!to || k <= to); });
    const groups = {};
    ok.forEach(o => { const k = dayKey(orderIso(o)); const g = mode === 'day' ? k : mode === 'month' ? k.slice(0, 7) : k.slice(0, 4); groups[g] = groups[g] || { n: 0, r: 0 }; groups[g].n++; groups[g].r += o.totals.total; });
    const keys = Object.keys(groups).sort().reverse();
    const byProd = {}, byCat = {}; const prods = getProducts();
    ok.forEach(o => o.items.forEach(i => { const p = prods.find(x => x.id === i.id); const cn = p ? p.catName : 'Khác'; byProd[i.name] = (byProd[i.name] || 0) + i.qty * i.price; byCat[cn] = (byCat[cn] || 0) + i.qty * i.price; }));
    const rank = o => Object.entries(o).sort((a, b) => b[1] - a[1]).slice(0, 8);
    const hb = list => list.length ? list.map(x => '<div class="hbar"><span class="nm" title="' + esc(x[0]) + '">' + esc(x[0]) + '</span><div class="tr"><i style="width:' + x[1] / list[0][1] * 100 + '%"></i></div><b>' + fmt(x[1]) + '</b></div>').join('') : empty('fa-chart-simple', 'Chưa có dữ liệu');
    $('#stBody').innerHTML = '<div class="grid g2" style="margin-bottom:18px"><div class="card"><h3>Theo sản phẩm</h3>' + hb(rank(byProd)) + '</div><div class="card"><h3>Theo danh mục</h3>' + hb(rank(byCat)) + '</div></div>' +
      '<div class="card"><h3>Doanh thu theo ' + { day: 'ngày', month: 'tháng', year: 'năm' }[mode] + ' <span class="grow" style="flex:1"></span><button class="btn btn-o btn-s" id="stCsv"><i class="fas fa-file-csv"></i> Xuất CSV</button></h3>' +
      (keys.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Kỳ</th><th class="num">Số đơn</th><th class="num">Doanh thu</th></tr>' + keys.map(k => '<tr><td>' + k + '</td><td class="num">' + groups[k].n + '</td><td class="num"><b>' + fmt(groups[k].r) + '</b></td></tr>').join('') + '<tr><td><b>Tổng</b></td><td class="num"><b>' + ok.length + '</b></td><td class="num"><b>' + fmt(revenueOf(ok)) + '</b></td></tr></table></div>' : empty('fa-chart-line', 'Không có đơn hàng trong khoảng này')) + '</div>';
    const b = $('#stCsv'); if (b) b.onclick = () => csvDownload('thong-ke-doanh-thu.csv', [['Kỳ', 'Số đơn', 'Doanh thu']].concat(keys.map(k => [k, groups[k].n, groups[k].r])));
  };
  v.innerHTML = '<div class="toolbar"><div class="tabs" style="margin:0">' + [['day', 'Theo ngày'], ['month', 'Theo tháng'], ['year', 'Theo năm']].map(x => '<button data-m="' + x[0] + '" class="' + (x[0] === mode ? 'on' : '') + '">' + x[1] + '</button>').join('') + '</div><span class="grow"></span><label>Từ</label><input type="date" class="input" id="stFrom" style="max-width:160px"><label>Đến</label><input type="date" class="input" id="stTo" style="max-width:160px"></div><div id="stBody"></div>';
  $$('.tabs button', v).forEach(b => b.onclick = () => { mode = b.dataset.m; $$('.tabs button', v).forEach(x => x.classList.toggle('on', x === b)); draw(); });
  $('#stFrom').onchange = e => { from = e.target.value; draw(); }; $('#stTo').onchange = e => { to = e.target.value; draw(); };
  draw();
};

/* ============================================================
   ĐƠN HÀNG
   ============================================================ */
function updateOrder(code, fn) { const l = getOrders(); const o = l.find(x => x.code === code); if (!o) return null; fn(o); save('orders', l); return o; }
function changeStatus(code, status) {
  const cur = getOrders().find(o => o.code === code); if (!cur) return;
  if (cur.status === 'Đã hủy' || cur.status === 'Hoàn thành') return toast('Đơn đã đóng, không thể đổi trạng thái', 'err');
  updateOrder(code, o => {
    o.history = o.history || []; o.history.push({ at: new Date().toISOString(), status, by: me().email });
    if (status === 'Đã hủy') { o.items.forEach(it => { const p = getProducts().find(x => x.id === it.id); if (p) setStock(it.id, p.stock + it.qty, 'Nhập kho', 'Hủy đơn ' + o.code); }); if (o.payStatus === 'Đã thanh toán') o.payStatus = 'Đã hoàn tiền'; }
    if (status === 'Hoàn thành' && o.pay === 'cod') o.payStatus = 'Đã thanh toán';
    o.status = status;
  });
  log('Cập nhật đơn ' + code, status); toast('Đã cập nhật: ' + status);
}
function orderDetail(code, after) {
  const o = getOrders().find(x => x.code === code); if (!o) return;
  const c = o.customer || {}, t = o.totals || {};
  const next = ORDER_FLOW[ORDER_FLOW.indexOf(o.status) + 1];
  const body = '<div class="grid g2" style="margin-bottom:16px"><div><h4 style="margin-bottom:8px">Khách hàng</h4><dl class="kv"><dt>Họ tên</dt><dd>' + esc(c.name) + '</dd><dt>Điện thoại</dt><dd>' + esc(c.phone) + '</dd><dt>Email</dt><dd>' + esc(c.email || '-') + '</dd><dt>Địa chỉ</dt><dd>' + esc(c.addr) + '</dd><dt>Ghi chú</dt><dd>' + esc(c.note || '-') + '</dd></dl></div>' +
    '<div><h4 style="margin-bottom:8px">Đơn hàng</h4><dl class="kv"><dt>Mã đơn</dt><dd><b>#' + o.code + '</b></dd><dt>Ngày đặt</dt><dd>' + esc(o.date) + '</dd><dt>Trạng thái</dt><dd>' + orderBadge(o.status) + '</dd><dt>Thanh toán</dt><dd>' + esc(PAY_NAMES[o.pay] || o.pay) + ' · ' + payBadge(o.payStatus || 'Chưa thanh toán') + '</dd></dl></div></div>' +
    '<div class="tbl-wrap"><table class="tbl"><tr><th>Sản phẩm</th><th class="num">Đơn giá</th><th class="num">SL</th><th class="num">Thành tiền</th></tr>' + o.items.map(i => { const p = ALL_PRODUCTS.find(x => x.id === i.id) || getProducts().find(x => x.id === i.id); return '<tr><td><div class="cell"><div class="thumb">' + (p ? imgTag(p) : '') + '</div><b>' + esc(i.name) + '</b></div></td><td class="num">' + fmt(i.price) + '</td><td class="num">' + i.qty + '</td><td class="num">' + fmt(i.price * i.qty) + '</td></tr>'; }).join('') + '</table></div>' +
    '<dl class="kv" style="margin-top:14px;max-width:340px;margin-left:auto"><dt>Tạm tính</dt><dd class="num">' + fmt(t.sub) + '</dd><dt>Giảm giá</dt><dd class="num">-' + fmt(t.discount) + '</dd>' + (t.tierDiscount ? '<dt>Ưu đãi hạng</dt><dd class="num">-' + fmt(t.tierDiscount) + '</dd>' : '') + '<dt>Vận chuyển</dt><dd class="num">' + fmt(t.ship) + '</dd><dt><b>Tổng cộng</b></dt><dd class="num"><b>' + fmt(t.total) + '</b></dd></dl>' +
    '<h4 style="margin:16px 0 8px">Lịch sử xử lý</h4><div class="timeline">' + (o.history || [{ at: orderIso(o), status: o.status, by: 'Khách hàng' }]).map(h => '<div><b>' + esc(h.status) + '</b> · ' + dt(h.at) + ' · ' + esc(h.by) + '</div>').join('') + '</div>';
  const closed = o.status === 'Đã hủy' || o.status === 'Hoàn thành';
  const m = modal('Đơn hàng #' + o.code, body, (closed ? '' : '<button class="btn btn-d" id="odCancel"><i class="fas fa-ban"></i> Hủy đơn</button>') + (next ? '<button class="btn btn-p" id="odNext"><i class="fas fa-arrow-right"></i> ' + next + '</button>' : '') + '<button class="btn btn-o" data-close>Đóng</button>', 'lg');
  const nx = $('#odNext', m), cx = $('#odCancel', m);
  if (nx) nx.onclick = () => { changeStatus(code, next); closeModal(); after(); };
  if (cx) cx.onclick = () => confirmBox('Hủy đơn #' + code + '? Tồn kho sẽ được hoàn lại.', () => { changeStatus(code, 'Đã hủy'); closeModal(); after(); });
}
PAGES.orders = v => {
  let q = '', st = '', page = 1;
  const draw = () => {
    const list = getOrders().filter(o => (!st || o.status === st) && (!q || (o.code + ' ' + (o.customer || {}).name + ' ' + (o.customer || {}).phone).toLowerCase().includes(q)));
    const pg = pager(list, page, 10);
    $('#odBody').innerHTML = list.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Mã đơn</th><th>Khách hàng</th><th>Ngày</th><th class="num">Tổng tiền</th><th>Thanh toán</th><th>Trạng thái</th><th></th></tr>' +
      pg.items.map(o => '<tr><td><b>#' + o.code + '</b></td><td><div class="cell"><div><b>' + esc((o.customer || {}).name) + '</b><small>' + esc((o.customer || {}).phone) + '</small></div></div></td><td>' + esc(o.date) + '</td><td class="num"><b>' + fmt(o.totals.total) + '</b></td><td>' + payBadge(o.payStatus || 'Chưa thanh toán') + '</td><td>' + orderBadge(o.status) + '</td><td class="act"><button class="btn btn-o btn-s" data-view="' + o.code + '"><i class="fas fa-eye"></i> Chi tiết</button></td></tr>').join('') + '</table></div>' + pagerHTML(pg) : '<div class="card">' + empty('fa-receipt', 'Chưa có đơn hàng nào. Đơn đặt từ trang cửa hàng sẽ xuất hiện ở đây.') + '</div>';
    $$('[data-view]', v).forEach(b => b.onclick = () => orderDetail(b.dataset.view, () => { route(); }));
    $$('[data-pg]', v).forEach(b => b.onclick = () => { page = +b.dataset.pg; draw(); });
  };
  v.innerHTML = '<div class="toolbar"><input class="input" id="odQ" placeholder="Tìm mã đơn, tên, số điện thoại..."><select class="select" id="odSt"><option value="">Tất cả trạng thái</option>' + ORDER_FLOW.concat(['Đã hủy']).map(s => '<option>' + s + '</option>').join('') + '</select><span class="grow"></span><button class="btn btn-o" id="odCsv"><i class="fas fa-file-csv"></i> Xuất CSV</button></div><div id="odBody"></div>';
  $('#odQ').oninput = e => { q = e.target.value.trim().toLowerCase(); page = 1; draw(); }; $('#odSt').onchange = e => { st = e.target.value; page = 1; draw(); };
  $('#odCsv').onclick = () => csvDownload('don-hang.csv', [['Mã', 'Ngày', 'Khách', 'SĐT', 'Tổng', 'Thanh toán', 'Trạng thái']].concat(getOrders().map(o => [o.code, o.date, (o.customer || {}).name, (o.customer || {}).phone, o.totals.total, o.payStatus, o.status])));
  draw();
};

/* ============================================================
   THANH TOÁN
   ============================================================ */
PAGES.payments = v => {
  let st = '';
  const draw = () => {
    const list = getOrders().filter(o => !st || (o.payStatus || 'Chưa thanh toán') === st);
    const paid = revenueOf(getOrders().filter(o => o.payStatus === 'Đã thanh toán')), pend = revenueOf(getOrders().filter(o => (o.payStatus || 'Chưa thanh toán') === 'Chưa thanh toán' && o.status !== 'Đã hủy')), ref = revenueOf(getOrders().filter(o => o.payStatus === 'Đã hoàn tiền'));
    $('#pyBody').innerHTML = '<div class="grid g3" style="margin-bottom:16px"><div class="card kpi"><div class="ic" style="background:var(--ok)"><i class="fas fa-check"></i></div><div><small>Đã thu</small><b>' + fmt(paid) + '</b></div></div><div class="card kpi"><div class="ic" style="background:var(--warn)"><i class="fas fa-hourglass-half"></i></div><div><small>Chờ thu</small><b>' + fmt(pend) + '</b></div></div><div class="card kpi"><div class="ic" style="background:var(--info)"><i class="fas fa-rotate-left"></i></div><div><small>Đã hoàn tiền</small><b>' + fmt(ref) + '</b></div></div></div>' +
      (list.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Mã giao dịch</th><th>Đơn hàng</th><th>Phương thức</th><th class="num">Số tiền</th><th>Trạng thái</th><th></th></tr>' + list.map(o => '<tr><td>TXN-' + o.code.slice(3) + '</td><td>#' + o.code + ' ' + orderBadge(o.status) + '</td><td>' + esc(PAY_NAMES[o.pay] || o.pay) + '</td><td class="num"><b>' + fmt(o.totals.total) + '</b></td><td>' + payBadge(o.payStatus || 'Chưa thanh toán') + '</td><td class="act">' +
        ((o.payStatus || 'Chưa thanh toán') === 'Chưa thanh toán' && o.status !== 'Đã hủy' ? '<button class="btn btn-ok btn-s" data-paid="' + o.code + '">Xác nhận đã thu</button>' : '') + (o.payStatus === 'Đã thanh toán' ? '<button class="btn btn-d btn-s" data-refund="' + o.code + '">Hoàn tiền</button>' : '') + '</td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-credit-card', 'Chưa có giao dịch') + '</div>');
    $$('[data-paid]', v).forEach(b => b.onclick = () => { updateOrder(b.dataset.paid, o => { o.payStatus = 'Đã thanh toán'; }); log('Xác nhận thu tiền', b.dataset.paid); toast('Đã xác nhận thanh toán'); draw(); });
    $$('[data-refund]', v).forEach(b => b.onclick = () => confirmBox('Hoàn tiền đơn #' + b.dataset.refund + '?', () => { updateOrder(b.dataset.refund, o => { o.payStatus = 'Đã hoàn tiền'; }); log('Hoàn tiền', b.dataset.refund); toast('Đã đánh dấu hoàn tiền'); draw(); }));
  };
  v.innerHTML = '<div class="note"><i class="fas fa-info-circle"></i> Đây là sổ theo dõi giao dịch thủ công. Chưa tích hợp cổng thanh toán thật (VNPay/MoMo) — cần backend để xác thực giao dịch tự động.</div><div class="toolbar"><select class="select" id="pySt"><option value="">Tất cả giao dịch</option><option>Chưa thanh toán</option><option>Đã thanh toán</option><option>Đã hoàn tiền</option></select></div><div id="pyBody"></div>';
  $('#pySt').onchange = e => { st = e.target.value; draw(); }; draw();
};

/* ============================================================
   SẢN PHẨM
   ============================================================ */
function buildProduct(f, old) {
  const cat = getCats().find(c => c.key === f.cat) || { name: f.cat };
  const info = (typeof CAT_INFO !== 'undefined' && CAT_INFO[f.cat]) || { features: ['Sản phẩm chính hãng, kiểm tra kỹ trước khi giao'], specs: [['Loại sản phẩm', cat.name]] };
  const id = old ? old.id : getProducts().reduce((m, p) => Math.max(m, p.id), 0) + 1;
  const sku = old ? old.sku : f.brand.substring(0, 3).toUpperCase() + '-' + String(1000 + id);
  const base = old || { rating: 4.5, reviews: 0, sold: 0 };
  return Object.assign({}, base, {
    id, name: f.name, cat: f.cat, catName: cat.name, brand: f.brand, emoji: f.emoji || (cat.icon || '🎵'), price: f.price, old: f.old || null, badge: f.badge || null, isNew: f.isNew, active: f.active,
    sku, stock: old ? old.stock : f.stock, short: f.short, img: f.img || '', images: f.images,
    desc: f.desc || (f.name + ' là sản phẩm chính hãng của ' + f.brand + ', được Soundora kiểm tra kỹ trước khi giao, bảo hành đầy đủ và đổi trả trong 30 ngày.'),
    features: old && old.features ? old.features : [f.short].concat(info.features, ['Cam kết 100% chính hãng ' + f.brand + ', bảo hành 12 tháng, đổi mới trong 30 ngày']),
    specs: old && old.specs ? old.specs : [['Thương hiệu', f.brand], ['Mã sản phẩm (SKU)', sku]].concat(info.specs)
  });
}
function productForm(p, done) {
  const isNew = !p; p = p || { cat: getCats()[0].key, brand: getBrands()[0].name, active: true, stock: 10, price: 0 };
  const body = '<div class="fg"><label>Tên sản phẩm *</label><input class="input" id="pfName" value="' + esc(p.name || '') + '"></div>' +
    '<div class="frow"><div class="fg"><label>Danh mục *</label><select class="select" id="pfCat">' + getCats().map(c => '<option value="' + c.key + '"' + (c.key === p.cat ? ' selected' : '') + '>' + esc(c.name) + '</option>').join('') + '</select></div><div class="fg"><label>Thương hiệu *</label><select class="select" id="pfBrand">' + getBrands().map(b => '<option' + (b.name === p.brand ? ' selected' : '') + '>' + esc(b.name) + '</option>').join('') + '</select></div></div>' +
    '<div class="frow3"><div class="fg"><label>Giá bán (đ) *</label><input class="input" type="number" min="0" id="pfPrice" value="' + (p.price || '') + '"></div><div class="fg"><label>Giá gốc (đ)</label><input class="input" type="number" min="0" id="pfOld" value="' + (p.old || '') + '"><small>Để trống nếu không giảm giá</small></div><div class="fg"><label>' + (isNew ? 'Tồn kho ban đầu' : 'Tồn kho') + '</label><input class="input" type="number" min="0" id="pfStock" value="' + p.stock + '"' + (isNew ? '' : ' disabled title="Điều chỉnh ở mục Kho hàng"') + '>' + (isNew ? '' : '<small>Điều chỉnh tại mục Kho hàng</small>') + '</div></div>' +
    '<div class="frow3"><div class="fg"><label>Nhãn</label><select class="select" id="pfBadge"><option value="">Không</option><option value="hot"' + (p.badge === 'hot' ? ' selected' : '') + '>Bán chạy</option><option value="new"' + (p.badge === 'new' ? ' selected' : '') + '>Mới</option><option value="sale"' + (p.badge === 'sale' ? ' selected' : '') + '>Giảm giá</option></select></div><div class="fg"><label>Emoji dự phòng</label><input class="input" id="pfEmoji" value="' + esc(p.emoji || '') + '" maxlength="4"></div><div class="fg"><label>&nbsp;</label><label class="switch"><input type="checkbox" id="pfActive"' + (p.active !== false ? ' checked' : '') + '> Đang bán</label> <label class="switch" style="margin-left:10px"><input type="checkbox" id="pfNew"' + (p.isNew ? ' checked' : '') + '> Hàng mới</label></div></div>' +
    '<div class="fg"><label>Mô tả ngắn *</label><input class="input" id="pfShort" value="' + esc(p.short || '') + '"></div><div class="fg"><label>Mô tả chi tiết</label><textarea class="textarea" rows="3" id="pfDesc">' + esc(p.desc || '') + '</textarea></div>' +
    '<div class="fg"><label>Ảnh sản phẩm</label><div class="img-prev"><img id="pfPrev" alt="" src="' + esc(p.img || (p.name ? svgArt(p) : '')) + '"><div style="flex:1"><input class="input" id="pfImg" placeholder="Đường dẫn ảnh (vd: images/products/f310.jpg) hoặc tải ảnh lên" value="' + esc(p.img && p.img.indexOf('data:') !== 0 ? p.img : '') + '"><input type="file" accept="image/*" id="pfFile" style="margin-top:8px"><small>Ảnh tải lên được lưu trong trình duyệt (tối đa 600KB). Nếu để trống, hệ thống tự dùng ảnh minh họa theo loại nhạc cụ.</small></div></div></div>' +
    '<div class="fg"><label>Ảnh phụ (mỗi dòng 1 đường dẫn)</label><textarea class="textarea" rows="2" id="pfImgs">' + esc((p.images || []).join('\n')) + '</textarea></div>';
  const m = modal(isNew ? 'Thêm sản phẩm' : 'Sửa sản phẩm', body, '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="pfSave"><i class="fas fa-save"></i> Lưu</button>', 'lg');
  let uploaded = p.img && p.img.indexOf('data:') === 0 ? p.img : '';
  $('#pfFile', m).onchange = e => readImage(e.target.files[0], d => { uploaded = d; $('#pfPrev', m).src = d; });
  $('#pfImg', m).oninput = e => { if (e.target.value.trim()) { uploaded = ''; $('#pfPrev', m).src = e.target.value.trim(); } };
  $('#pfPrev', m).onerror = function () { this.onerror = null; this.src = svgArt({ cat: $('#pfCat', m).value, brand: $('#pfBrand', m).value }); };
  $('#pfSave', m).onclick = () => {
    const f = { name: $('#pfName', m).value.trim(), cat: $('#pfCat', m).value, brand: $('#pfBrand', m).value, price: +$('#pfPrice', m).value, old: +$('#pfOld', m).value, stock: Math.max(0, parseInt($('#pfStock', m).value) || 0), badge: $('#pfBadge', m).value, emoji: $('#pfEmoji', m).value.trim(), active: $('#pfActive', m).checked, isNew: $('#pfNew', m).checked, short: $('#pfShort', m).value.trim(), desc: $('#pfDesc', m).value.trim(), img: uploaded || $('#pfImg', m).value.trim(), images: $('#pfImgs', m).value.split('\n').map(x => x.trim()).filter(Boolean) };
    if (f.name.length < 3) return toast('Tên sản phẩm tối thiểu 3 ký tự', 'err');
    if (!(f.price > 0)) return toast('Giá bán phải lớn hơn 0', 'err');
    if (f.old && f.old <= f.price) return toast('Giá gốc phải lớn hơn giá bán (hoặc để trống)', 'err');
    if (f.short.length < 5) return toast('Vui lòng nhập mô tả ngắn', 'err');
    const list = getProducts();
    const np = buildProduct(f, isNew ? null : p);
    if (isNew) list.push(np); else list[list.findIndex(x => x.id === p.id)] = np;
    saveProducts(list);
    if (isNew) { const l = load('a_invlog', []); const sm = load('a_stock', {}); sm[np.id] = np.stock; save('a_stock', sm); l.unshift({ at: new Date().toISOString(), id: np.id, name: np.name, type: 'Nhập kho', qty: np.stock, before: 0, after: np.stock, note: 'Tạo sản phẩm mới', by: me().email }); save('a_invlog', l); }
    log(isNew ? 'Thêm sản phẩm' : 'Sửa sản phẩm', np.name); toast('Đã lưu sản phẩm'); closeModal(); done();
  };
}
PAGES.products = v => {
  let q = '', cat = '', page = 1;
  const draw = () => {
    const list = getProducts().filter(p => (!cat || p.cat === cat) && (!q || (p.name + ' ' + p.brand + ' ' + p.sku).toLowerCase().includes(q)));
    const pg = pager(list, page, 10);
    $('#prBody').innerHTML = list.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Sản phẩm</th><th>Danh mục</th><th class="num">Giá</th><th class="num">Tồn</th><th>Trạng thái</th><th></th></tr>' + pg.items.map(p => '<tr><td><div class="cell"><div class="thumb">' + imgTag(p) + '</div><div><b>' + esc(p.name) + '</b><small>' + esc(p.brand) + ' · ' + esc(p.sku) + '</small></div></div></td><td>' + esc(p.catName) + '</td><td class="num"><b>' + fmt(p.price) + '</b>' + (p.old ? '<br><small style="text-decoration:line-through;color:var(--mute)">' + fmt(p.old) + '</small>' : '') + '</td><td class="num">' + (p.stock <= 0 ? badge('Hết', 'bad') : p.stock <= 5 ? badge(p.stock, 'warn') : p.stock) + '</td><td>' + (p.active === false ? badge('Đang ẩn', 'mute') : badge('Đang bán', 'ok')) + '</td><td class="act"><button class="btn btn-o btn-s" data-edit="' + p.id + '"><i class="fas fa-pen"></i></button><button class="btn btn-d btn-s" data-del="' + p.id + '"><i class="fas fa-trash"></i></button></td></tr>').join('') + '</table></div>' + pagerHTML(pg) : '<div class="card">' + empty('fa-guitar', 'Không có sản phẩm phù hợp') + '</div>';
    $$('[data-edit]', v).forEach(b => b.onclick = () => productForm(getProducts().find(x => x.id === +b.dataset.edit), draw));
    $$('[data-del]', v).forEach(b => b.onclick = () => { const p = getProducts().find(x => x.id === +b.dataset.del); confirmBox('Xóa sản phẩm <b>' + esc(p.name) + '</b>? Nếu chỉ muốn ngừng bán, hãy chọn "Đang bán = tắt" khi sửa.', () => { saveProducts(getProducts().filter(x => x.id !== p.id)); log('Xóa sản phẩm', p.name); toast('Đã xóa'); draw(); }); });
    $$('[data-pg]', v).forEach(b => b.onclick = () => { page = +b.dataset.pg; draw(); });
  };
  v.innerHTML = '<div class="toolbar"><input class="input" id="prQ" placeholder="Tìm tên, thương hiệu, SKU..."><select class="select" id="prCat"><option value="">Tất cả danh mục</option>' + getCats().map(c => '<option value="' + c.key + '">' + esc(c.name) + '</option>').join('') + '</select><span class="grow"></span><button class="btn btn-p" id="prAdd"><i class="fas fa-plus"></i> Thêm sản phẩm</button></div><div id="prBody"></div>';
  $('#prQ').oninput = e => { q = e.target.value.trim().toLowerCase(); page = 1; draw(); }; $('#prCat').onchange = e => { cat = e.target.value; page = 1; draw(); }; $('#prAdd').onclick = () => productForm(null, draw); draw();
};

/* ============================================================
   DANH MỤC & THƯƠNG HIỆU
   ============================================================ */
PAGES.categories = v => {
  const draw = () => {
    const cats = getCats(), prods = getProducts();
    v.innerHTML = '<div class="toolbar"><span class="grow"></span><button class="btn btn-p" id="ctAdd"><i class="fas fa-plus"></i> Thêm danh mục</button></div><div class="tbl-wrap"><table class="tbl"><tr><th>Danh mục</th><th>Mã</th><th class="num">Sản phẩm</th><th>Hiển thị</th><th></th></tr>' + cats.map(c => '<tr><td><b>' + (c.icon || '') + ' ' + esc(c.name) + '</b></td><td>' + esc(c.key) + '</td><td class="num">' + prods.filter(p => p.cat === c.key).length + '</td><td><label class="switch"><input type="checkbox" data-tg="' + c.key + '"' + (c.active !== false ? ' checked' : '') + '></label></td><td class="act"><button class="btn btn-o btn-s" data-edit="' + c.key + '"><i class="fas fa-pen"></i></button><button class="btn btn-d btn-s" data-del="' + c.key + '"><i class="fas fa-trash"></i></button></td></tr>').join('') + '</table></div>';
    const form = c => { const isNew = !c; const m = modal(isNew ? 'Thêm danh mục' : 'Sửa danh mục', '<div class="frow"><div class="fg"><label>Tên *</label><input class="input" id="cfName" value="' + esc(c ? c.name : '') + '"></div><div class="fg"><label>Biểu tượng (emoji)</label><input class="input" id="cfIcon" maxlength="4" value="' + esc(c ? c.icon : '🎵') + '"></div></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="cfSave">Lưu</button>', 'sm');
      $('#cfSave', m).onclick = () => { const name = $('#cfName', m).value.trim(); if (name.length < 2) return toast('Nhập tên danh mục', 'err'); const list = getCats(); const icon = $('#cfIcon', m).value.trim() || '🎵';
        if (isNew) { const key = slug(name); if (!key || list.some(x => x.key === key)) return toast('Danh mục đã tồn tại', 'err'); list.push({ key, name, icon, active: true }); }
        else { const t = list.find(x => x.key === c.key); t.name = name; t.icon = icon; const pl = getProducts(); pl.forEach(p => { if (p.cat === c.key) p.catName = name; }); saveProducts(pl); }
        save('a_categories', list); log(isNew ? 'Thêm danh mục' : 'Sửa danh mục', name); toast('Đã lưu'); closeModal(); draw(); }; };
    $('#ctAdd').onclick = () => form(null);
    $$('[data-edit]', v).forEach(b => b.onclick = () => form(getCats().find(x => x.key === b.dataset.edit)));
    $$('[data-tg]', v).forEach(b => b.onchange = () => { const l = getCats(); l.find(x => x.key === b.dataset.tg).active = b.checked; save('a_categories', l); log('Bật/tắt danh mục', b.dataset.tg); toast(b.checked ? 'Đã hiện danh mục' : 'Đã ẩn danh mục (sản phẩm thuộc danh mục cũng ẩn)'); });
    $$('[data-del]', v).forEach(b => b.onclick = () => { const n = getProducts().filter(p => p.cat === b.dataset.del).length; if (n) return toast('Không thể xóa: còn ' + n + ' sản phẩm thuộc danh mục này', 'err'); confirmBox('Xóa danh mục này?', () => { save('a_categories', getCats().filter(x => x.key !== b.dataset.del)); log('Xóa danh mục', b.dataset.del); draw(); }); });
  }; draw();
};
PAGES.brands = v => {
  const draw = () => {
    const brands = getBrands(), prods = getProducts();
    v.innerHTML = '<div class="toolbar"><span class="grow"></span><button class="btn btn-p" id="brAdd"><i class="fas fa-plus"></i> Thêm thương hiệu</button></div><div class="tbl-wrap"><table class="tbl"><tr><th>Thương hiệu</th><th>Xuất xứ</th><th>Mô tả</th><th class="num">Sản phẩm liên kết</th><th></th></tr>' + brands.map((b, i) => '<tr><td><b>' + esc(b.name) + '</b></td><td>' + esc(b.origin) + '</td><td style="max-width:320px"><small>' + esc(b.desc) + '</small></td><td class="num">' + prods.filter(p => p.brand === b.name).length + '</td><td class="act"><button class="btn btn-o btn-s" data-edit="' + i + '"><i class="fas fa-pen"></i></button><button class="btn btn-d btn-s" data-del="' + i + '"><i class="fas fa-trash"></i></button></td></tr>').join('') + '</table></div>';
    const form = i => { const b = i == null ? null : getBrands()[i]; const m = modal(b ? 'Sửa thương hiệu' : 'Thêm thương hiệu', '<div class="frow"><div class="fg"><label>Tên *</label><input class="input" id="bfName" value="' + esc(b ? b.name : '') + '"></div><div class="fg"><label>Xuất xứ</label><input class="input" id="bfOrigin" value="' + esc(b ? b.origin : '') + '"></div></div><div class="fg"><label>Mô tả</label><textarea class="textarea" rows="3" id="bfDesc">' + esc(b ? b.desc : '') + '</textarea></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="bfSave">Lưu</button>', 'sm');
      $('#bfSave', m).onclick = () => { const name = $('#bfName', m).value.trim(); if (name.length < 2) return toast('Nhập tên thương hiệu', 'err'); const list = getBrands(); if (list.some((x, k) => x.name.toLowerCase() === name.toLowerCase() && k !== i)) return toast('Thương hiệu đã tồn tại', 'err');
        const nb = { name, origin: $('#bfOrigin', m).value.trim(), desc: $('#bfDesc', m).value.trim() };
        if (b) { const pl = getProducts(); pl.forEach(p => { if (p.brand === b.name) p.brand = name; }); saveProducts(pl); list[i] = nb; } else list.push(nb);
        save('a_brands', list); log(b ? 'Sửa thương hiệu' : 'Thêm thương hiệu', name); toast('Đã lưu'); closeModal(); draw(); }; };
    $('#brAdd').onclick = () => form(null);
    $$('[data-edit]', v).forEach(b => b.onclick = () => form(+b.dataset.edit));
    $$('[data-del]', v).forEach(b => b.onclick = () => { const br = getBrands()[+b.dataset.del]; const n = getProducts().filter(p => p.brand === br.name).length; if (n) return toast('Không thể xóa: còn ' + n + ' sản phẩm của thương hiệu này', 'err'); confirmBox('Xóa thương hiệu <b>' + esc(br.name) + '</b>?', () => { const l = getBrands(); l.splice(+b.dataset.del, 1); save('a_brands', l); log('Xóa thương hiệu', br.name); draw(); }); });
  }; draw();
};

/* ============================================================
   KHO HÀNG
   ============================================================ */
PAGES.inventory = v => {
  let tab = 'stock', q = '';
  const draw = () => {
    if (tab === 'stock') {
      const list = getProducts().filter(p => !q || (p.name + p.sku).toLowerCase().includes(q));
      $('#ivBody').innerHTML = '<div class="tbl-wrap"><table class="tbl"><tr><th>Sản phẩm</th><th class="num">Tồn kho</th><th>Tình trạng</th><th></th></tr>' + list.map(p => '<tr><td><div class="cell"><div class="thumb">' + imgTag(p) + '</div><div><b>' + esc(p.name) + '</b><small>' + esc(p.sku) + '</small></div></div></td><td class="num"><b>' + p.stock + '</b></td><td>' + (p.stock <= 0 ? badge('Hết hàng', 'bad') : p.stock <= 5 ? badge('Sắp hết', 'warn') : badge('Ổn định', 'ok')) + '</td><td class="act"><button class="btn btn-ok btn-s" data-in="' + p.id + '">+ Nhập</button><button class="btn btn-o btn-s" data-out="' + p.id + '">- Xuất</button><button class="btn btn-o btn-s" data-adj="' + p.id + '">Điều chỉnh</button></td></tr>').join('') + '</table></div>';
      const act = (id, kind) => { const p = getProducts().find(x => x.id === id); const lbl = { in: 'Nhập kho', out: 'Xuất kho', adj: 'Điều chỉnh tồn kho' }[kind];
        const m = modal(lbl + ': ' + esc(p.name), '<p style="margin-bottom:12px">Tồn hiện tại: <b>' + p.stock + '</b></p><div class="fg"><label>' + (kind === 'adj' ? 'Số lượng tồn mới' : 'Số lượng') + '</label><input class="input" type="number" min="0" id="ivQty" value="' + (kind === 'adj' ? p.stock : 1) + '"></div><div class="fg"><label>Ghi chú</label><input class="input" id="ivNote" placeholder="Nhà cung cấp, lý do..."></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="ivOk">Xác nhận</button>', 'sm');
        $('#ivOk', m).onclick = () => { const n = parseInt($('#ivQty', m).value); if (isNaN(n) || n < 0) return toast('Số lượng không hợp lệ', 'err'); let next = kind === 'in' ? p.stock + n : kind === 'out' ? p.stock - n : n; if (next < 0) return toast('Không thể xuất quá số lượng tồn', 'err'); setStock(id, next, lbl, $('#ivNote', m).value.trim()); log(lbl, p.name + ' → ' + next); toast('Đã cập nhật kho'); closeModal(); draw(); }; };
      $$('[data-in]', v).forEach(b => b.onclick = () => act(+b.dataset.in, 'in')); $$('[data-out]', v).forEach(b => b.onclick = () => act(+b.dataset.out, 'out')); $$('[data-adj]', v).forEach(b => b.onclick = () => act(+b.dataset.adj, 'adj'));
    } else {
      const l = load('a_invlog', []);
      $('#ivBody').innerHTML = l.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Thời gian</th><th>Sản phẩm</th><th>Loại</th><th class="num">Thay đổi</th><th class="num">Trước → Sau</th><th>Ghi chú</th><th>Người thực hiện</th></tr>' + l.slice(0, 100).map(x => '<tr><td>' + dt(x.at) + '</td><td>' + esc(x.name) + '</td><td>' + badge(x.type, x.qty >= 0 ? 'ok' : 'warn') + '</td><td class="num"><b>' + (x.qty > 0 ? '+' : '') + x.qty + '</b></td><td class="num">' + x.before + ' → ' + x.after + '</td><td>' + esc(x.note) + '</td><td>' + esc(x.by) + '</td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-clock-rotate-left', 'Chưa có lịch sử thay đổi kho') + '</div>';
    }
  };
  v.innerHTML = '<div class="toolbar"><div class="tabs" style="margin:0"><button data-t="stock" class="on">Tồn kho</button><button data-t="log">Lịch sử nhập / xuất</button></div><input class="input" id="ivQ" placeholder="Tìm sản phẩm..."></div><div id="ivBody"></div>';
  $$('.tabs button', v).forEach(b => b.onclick = () => { tab = b.dataset.t; $$('.tabs button', v).forEach(x => x.classList.toggle('on', x === b)); $('#ivQ').style.display = tab === 'stock' ? '' : 'none'; draw(); });
  $('#ivQ').oninput = e => { q = e.target.value.trim().toLowerCase(); draw(); }; draw();
};

/* ============================================================
   KHUYẾN MÃI
   ============================================================ */
PAGES.promotions = v => {
  const draw = () => {
    const cps = getCoupons(), tiers = getTiers();
    v.innerHTML = '<div class="toolbar"><span class="grow"></span><button class="btn btn-p" id="cpAdd"><i class="fas fa-plus"></i> Thêm mã giảm giá</button></div>' + (Object.keys(cps).length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Mã</th><th>Ưu đãi</th><th class="num">Đơn tối thiểu</th><th>Thời gian</th><th>Hạng áp dụng</th><th>Bật</th><th></th></tr>' + Object.keys(cps).map(k => { const c = cps[k]; return '<tr><td><b>' + esc(k) + '</b></td><td>' + esc(c.desc) + '</td><td class="num">' + fmt(c.min) + '</td><td>' + (c.start || c.end ? esc((c.start || '...') + ' → ' + (c.end || '...')) : 'Không giới hạn') + '</td><td>' + (c.tier && c.tier !== 'all' ? badge((tiers.find(t => t.key === c.tier) || { name: c.tier }).name + ' trở lên', 'p') : 'Tất cả') + '</td><td><label class="switch"><input type="checkbox" data-tg="' + esc(k) + '"' + (c.active !== false ? ' checked' : '') + '></label></td><td class="act"><button class="btn btn-o btn-s" data-edit="' + esc(k) + '"><i class="fas fa-pen"></i></button><button class="btn btn-d btn-s" data-del="' + esc(k) + '"><i class="fas fa-trash"></i></button></td></tr>'; }).join('') + '</table></div>' : '<div class="card">' + empty('fa-ticket', 'Chưa có mã giảm giá') + '</div>');
    const form = code => { const c = code ? getCoupons()[code] : { type: 'percent', value: 10, max: 0, min: 0, tier: 'all' };
      const m = modal(code ? 'Sửa mã ' + esc(code) : 'Thêm mã giảm giá', '<div class="frow"><div class="fg"><label>Mã *</label><input class="input" id="cpCode" value="' + esc(code || '') + '"' + (code ? ' disabled' : '') + ' style="text-transform:uppercase"></div><div class="fg"><label>Loại</label><select class="select" id="cpType"><option value="percent"' + (c.type === 'percent' ? ' selected' : '') + '>Giảm theo %</option><option value="amount"' + (c.type === 'amount' ? ' selected' : '') + '>Giảm số tiền</option><option value="ship"' + (c.type === 'ship' ? ' selected' : '') + '>Miễn phí vận chuyển</option></select></div></div>' +
        '<div class="frow3"><div class="fg"><label>Giá trị (% hoặc đ)</label><input class="input" type="number" min="0" id="cpVal" value="' + c.value + '"></div><div class="fg"><label>Giảm tối đa (đ)</label><input class="input" type="number" min="0" id="cpMax" value="' + (c.max || 0) + '"></div><div class="fg"><label>Đơn tối thiểu (đ)</label><input class="input" type="number" min="0" id="cpMin" value="' + c.min + '"></div></div>' +
        '<div class="frow3"><div class="fg"><label>Bắt đầu</label><input class="input" type="date" id="cpStart" value="' + esc(c.start || '') + '"></div><div class="fg"><label>Kết thúc</label><input class="input" type="date" id="cpEnd" value="' + esc(c.end || '') + '"></div><div class="fg"><label>Hạng khách hàng</label><select class="select" id="cpTier"><option value="all">Tất cả</option>' + tiers.map(t => '<option value="' + t.key + '"' + (c.tier === t.key ? ' selected' : '') + '>' + esc(t.name) + ' trở lên</option>').join('') + '</select></div></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="cpSave">Lưu</button>');
      $('#cpSave', m).onclick = () => { const k = (code || $('#cpCode', m).value.trim().toUpperCase()); if (!/^[A-Z0-9_-]{3,20}$/.test(k)) return toast('Mã gồm 3-20 ký tự chữ/số', 'err'); const all = getCoupons(); if (!code && all[k]) return toast('Mã đã tồn tại', 'err');
        const type = $('#cpType', m).value, value = +$('#cpVal', m).value, max = +$('#cpMax', m).value, min = +$('#cpMin', m).value, start = $('#cpStart', m).value, end = $('#cpEnd', m).value;
        if (type === 'percent' && (value <= 0 || value > 100)) return toast('Phần trăm phải từ 1 đến 100', 'err'); if (type === 'amount' && value <= 0) return toast('Số tiền giảm phải lớn hơn 0', 'err'); if (start && end && end < start) return toast('Ngày kết thúc phải sau ngày bắt đầu', 'err');
        const desc = type === 'ship' ? 'Miễn phí vận chuyển' : type === 'percent' ? 'Giảm ' + value + '%' + (max ? ' (tối đa ' + fmt(max) + ')' : '') : 'Giảm ' + fmt(value); const tier = $('#cpTier', m).value;
        all[k] = { type, value: type === 'ship' ? 0 : value, max: type === 'percent' ? max : 0, min, start, end, tier, active: code ? all[k].active !== false : true, desc: desc + (min ? ' cho đơn từ ' + fmt(min) : '') };
        save('a_coupons', all); log(code ? 'Sửa mã giảm giá' : 'Thêm mã giảm giá', k); toast('Đã lưu'); closeModal(); draw(); }; };
    $('#cpAdd').onclick = () => form(null); $$('[data-edit]', v).forEach(b => b.onclick = () => form(b.dataset.edit));
    $$('[data-tg]', v).forEach(b => b.onchange = () => { const all = getCoupons(); all[b.dataset.tg].active = b.checked; save('a_coupons', all); log('Bật/tắt mã', b.dataset.tg); toast(b.checked ? 'Đã bật mã' : 'Đã tắt mã'); });
    $$('[data-del]', v).forEach(b => b.onclick = () => confirmBox('Xóa mã <b>' + esc(b.dataset.del) + '</b>?', () => { const all = getCoupons(); delete all[b.dataset.del]; save('a_coupons', all); log('Xóa mã giảm giá', b.dataset.del); draw(); }));
  }; draw();
};

/* ============================================================
   KHÁCH HÀNG & HẠNG THÀNH VIÊN
   ============================================================ */
PAGES.customers = v => {
  let q = '';
  const draw = () => {
    const users = getUsers().filter(u => !q || (u.name + u.email + (u.phone || '')).toLowerCase().includes(q));
    $('#cuBody').innerHTML = users.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Khách hàng</th><th>Hạng</th><th class="num">Số đơn</th><th class="num">Tổng chi tiêu</th><th>Trạng thái</th><th></th></tr>' + users.map(u => {
      const sp = orderSpend(u.email), t = tierOf(sp), n = getOrders().filter(o => o.user === u.email).length;
      return '<tr><td><div class="cell"><div class="avatar" style="width:38px;height:38px">' + esc(u.name.charAt(0).toUpperCase()) + '</div><div><b>' + esc(u.name) + '</b><small>' + esc(u.email) + (u.phone ? ' · ' + esc(u.phone) : '') + '</small></div></div></td><td><span class="pill-lg" style="background:' + t.color + '">' + esc(t.name) + '</span></td><td class="num">' + n + '</td><td class="num"><b>' + fmt(sp) + '</b></td><td>' + (u.locked ? badge('Đã khóa', 'bad') : badge('Hoạt động', 'ok')) + '</td><td class="act"><button class="btn btn-o btn-s" data-view="' + esc(u.email) + '">Hồ sơ</button><button class="btn ' + (u.locked ? 'btn-ok' : 'btn-d') + ' btn-s" data-lock="' + esc(u.email) + '">' + (u.locked ? 'Mở khóa' : 'Khóa') + '</button></td></tr>';
    }).join('') + '</table></div>' : '<div class="card">' + empty('fa-users', 'Chưa có khách hàng đăng ký. Khách đăng ký ở trang cửa hàng sẽ xuất hiện tại đây.') + '</div>';
    $$('[data-lock]', v).forEach(b => b.onclick = () => { const l = getUsers(); const u = l.find(x => x.email === b.dataset.lock); u.locked = !u.locked; save('users', l); log(u.locked ? 'Khóa tài khoản' : 'Mở khóa tài khoản', u.email); toast(u.locked ? 'Đã khóa tài khoản' : 'Đã mở khóa'); draw(); });
    $$('[data-view]', v).forEach(b => b.onclick = () => { const u = getUsers().find(x => x.email === b.dataset.view); const os = getOrders().filter(o => o.user === u.email); const sp = orderSpend(u.email), t = tierOf(sp);
      modal('Hồ sơ khách hàng', '<dl class="kv" style="margin-bottom:16px"><dt>Họ tên</dt><dd>' + esc(u.name) + '</dd><dt>Email</dt><dd>' + esc(u.email) + '</dd><dt>Điện thoại</dt><dd>' + esc(u.phone || '-') + '</dd><dt>Ngày đăng ký</dt><dd>' + (u.created ? dt(u.created) : '-') + '</dd><dt>Hạng</dt><dd><span class="pill-lg" style="background:' + t.color + '">' + esc(t.name) + '</span> (ưu đãi ' + t.discount + '%)</dd><dt>Tổng chi tiêu</dt><dd><b>' + fmt(sp) + '</b></dd></dl><h4 style="margin-bottom:8px">Lịch sử mua hàng</h4>' + (os.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Mã</th><th>Ngày</th><th class="num">Tổng</th><th>Trạng thái</th></tr>' + os.map(o => '<tr><td>#' + o.code + '</td><td>' + esc(o.date) + '</td><td class="num">' + fmt(o.totals.total) + '</td><td>' + orderBadge(o.status) + '</td></tr>').join('') + '</table></div>' : '<p style="color:var(--mute)">Chưa có đơn hàng.</p>'), '<button class="btn btn-o" data-close>Đóng</button>'); });
  };
  v.innerHTML = '<div class="toolbar"><input class="input" id="cuQ" placeholder="Tìm tên, email, số điện thoại..."></div><div id="cuBody"></div>';
  $('#cuQ').oninput = e => { q = e.target.value.trim().toLowerCase(); draw(); }; draw();
};
PAGES.tiers = v => {
  const draw = () => {
    const tiers = getTiers().slice().sort((a, b) => a.min - b.min);
    const users = getUsers();
    v.innerHTML = '<div class="note"><i class="fas fa-info-circle"></i> Hạng được <b>tự động tính</b> theo tổng giá trị các đơn không bị hủy của khách. Ưu đãi hạng được trừ trực tiếp vào đơn khi khách đã đăng nhập.</div><div class="grid g3">' +
      tiers.map(t => '<div class="card" style="border-top:4px solid ' + t.color + '"><h3><span class="pill-lg" style="background:' + t.color + '"><i class="fas fa-crown"></i> ' + esc(t.name) + '</span></h3><dl class="kv"><dt>Chi tiêu từ</dt><dd><b>' + fmt(t.min) + '</b></dd><dt>Ưu đãi</dt><dd><b>' + t.discount + '%</b> mỗi đơn</dd><dt>Số khách</dt><dd>' + users.filter(u => tierOf(orderSpend(u.email)).key === t.key).length + '</dd></dl><div style="margin-top:14px"><button class="btn btn-o btn-s" data-edit="' + t.key + '"><i class="fas fa-pen"></i> Chỉnh quy tắc</button></div></div>').join('') + '</div>';
    $$('[data-edit]', v).forEach(b => b.onclick = () => { const all = getTiers(); const t = all.find(x => x.key === b.dataset.edit);
      const m = modal('Quy tắc hạng ' + esc(t.name), '<div class="frow"><div class="fg"><label>Chi tiêu tối thiểu (đ)</label><input class="input" type="number" min="0" id="tiMin" value="' + t.min + '"' + (t.min === 0 && all.indexOf(t) === 0 ? ' disabled' : '') + '></div><div class="fg"><label>Ưu đãi (%)</label><input class="input" type="number" min="0" max="30" id="tiDis" value="' + t.discount + '"></div></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="tiSave">Lưu</button>', 'sm');
      $('#tiSave', m).onclick = () => { const min = +$('#tiMin', m).value, dis = +$('#tiDis', m).value; if (dis < 0 || dis > 30) return toast('Ưu đãi từ 0 đến 30%', 'err'); t.min = t.min === 0 && all.indexOf(t) === 0 ? 0 : min; t.discount = dis; save('a_tiers', all); log('Sửa hạng thành viên', t.name); toast('Đã lưu'); closeModal(); draw(); }; });
  }; draw();
};

/* ============================================================
   ĐÁNH GIÁ
   ============================================================ */
function allReviews() {
  const out = [];
  getProducts().forEach(p => load('reviews_' + p.id, []).forEach((r, i) => out.push({ p, r, i })));
  return out;
}
PAGES.reviews = v => {
  let st = '';
  const draw = () => {
    const list = allReviews().filter(x => !st || (st === 'hidden' ? x.r.hidden : !x.r.hidden));
    $('#rvBody').innerHTML = list.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Sản phẩm</th><th>Khách hàng</th><th>Đánh giá</th><th>Nội dung</th><th>Trạng thái</th><th></th></tr>' + list.map(x => '<tr><td><div class="cell"><div class="thumb">' + imgTag(x.p) + '</div><b style="max-width:160px">' + esc(x.p.name) + '</b></div></td><td><b>' + esc(x.r.name) + '</b><br><small>' + esc(x.r.date) + '</small>' + (x.r.verified ? '<br>' + badge('Đã mua hàng', 'ok') : '<br>' + badge('Chưa xác minh mua', 'mute')) + '</td><td style="color:#F39C12;white-space:nowrap">' + '★'.repeat(x.r.rating) + '☆'.repeat(5 - x.r.rating) + '</td><td style="max-width:300px">' + esc(x.r.text) + '</td><td>' + (x.r.hidden ? badge('Đã ẩn', 'bad') : badge('Hiển thị', 'ok')) + '</td><td class="act"><button class="btn btn-o btn-s" data-hide="' + x.p.id + ':' + x.i + '">' + (x.r.hidden ? 'Hiện lại' : 'Ẩn') + '</button><button class="btn btn-d btn-s" data-del="' + x.p.id + ':' + x.i + '"><i class="fas fa-trash"></i></button></td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-star', 'Chưa có đánh giá nào từ khách hàng') + '</div>';
    $$('[data-hide]', v).forEach(b => b.onclick = () => { const [pid, i] = b.dataset.hide.split(':').map(Number); const l = load('reviews_' + pid, []); l[i].hidden = !l[i].hidden; save('reviews_' + pid, l); log(l[i].hidden ? 'Ẩn đánh giá' : 'Hiện đánh giá', 'SP #' + pid); toast('Đã cập nhật'); draw(); });
    $$('[data-del]', v).forEach(b => b.onclick = () => confirmBox('Xóa đánh giá này vĩnh viễn?', () => { const [pid, i] = b.dataset.del.split(':').map(Number); const l = load('reviews_' + pid, []); l.splice(i, 1); save('reviews_' + pid, l); log('Xóa đánh giá', 'SP #' + pid); draw(); }));
  };
  v.innerHTML = '<div class="note"><i class="fas fa-info-circle"></i> Đánh giá mẫu hiển thị sẵn trên trang chi tiết là dữ liệu minh họa nên không nằm trong danh sách duyệt này.</div><div class="toolbar"><select class="select" id="rvSt"><option value="">Tất cả</option><option value="show">Đang hiển thị</option><option value="hidden">Đã ẩn</option></select></div><div id="rvBody"></div>';
  $('#rvSt').onchange = e => { st = e.target.value; draw(); }; draw();
};

/* ============================================================
   LIÊN HỆ
   ============================================================ */
PAGES.contacts = v => {
  const SUB = { 'tu-van': 'Tư vấn sản phẩm', 'bao-hanh': 'Bảo hành', 'doi-tra': 'Đổi trả', 'gop-y': 'Góp ý', 'khac': 'Khác' };
  const draw = () => {
    const list = load('contacts', []).map((c, i) => Object.assign({ status: 'Mới', note: '' }, c, { i }));
    v.innerHTML = list.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Người gửi</th><th>Chủ đề</th><th>Nội dung</th><th>Trạng thái</th><th></th></tr>' + list.map(c => '<tr><td><b>' + esc(c.name) + '</b><br><small>' + esc(c.email) + ' · ' + esc(c.phone) + '</small><br><small>' + esc(c.date) + '</small></td><td>' + esc(SUB[c.subject] || c.subject) + '</td><td style="max-width:320px">' + esc(c.msg) + (c.note ? '<br><small style="color:var(--p)"><i class="fas fa-note-sticky"></i> ' + esc(c.note) + '</small>' : '') + '</td><td>' + badge(c.status, { 'Mới': 'warn', 'Đang xử lý': 'info', 'Đã xử lý': 'ok' }[c.status] || 'mute') + '</td><td class="act"><button class="btn btn-o btn-s" data-h="' + c.i + '">Xử lý</button><button class="btn btn-d btn-s" data-del="' + c.i + '"><i class="fas fa-trash"></i></button></td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-envelope-open', 'Chưa có tin nhắn nào từ khách hàng') + '</div>';
    $$('[data-h]', v).forEach(b => b.onclick = () => { const i = +b.dataset.h; const l = load('contacts', []); const c = Object.assign({ status: 'Mới', note: '' }, l[i]);
      const m = modal('Xử lý liên hệ', '<p style="margin-bottom:12px"><b>' + esc(c.name) + '</b> · ' + esc(c.phone) + '</p><p style="background:var(--bg);padding:12px;border-radius:10px;margin-bottom:14px">' + esc(c.msg) + '</p><div class="fg"><label>Trạng thái</label><select class="select" id="ctSt">' + ['Mới', 'Đang xử lý', 'Đã xử lý'].map(s => '<option' + (s === c.status ? ' selected' : '') + '>' + s + '</option>').join('') + '</select></div><div class="fg"><label>Ghi chú nội bộ</label><textarea class="textarea" rows="3" id="ctNote">' + esc(c.note) + '</textarea></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="ctSave">Lưu</button>', 'sm');
      $('#ctSave', m).onclick = () => { l[i] = Object.assign(l[i], { status: $('#ctSt', m).value, note: $('#ctNote', m).value.trim() }); save('contacts', l); log('Xử lý liên hệ', c.name); toast('Đã lưu'); closeModal(); draw(); }; });
    $$('[data-del]', v).forEach(b => b.onclick = () => confirmBox('Xóa tin nhắn này?', () => { const l = load('contacts', []); l.splice(+b.dataset.del, 1); save('contacts', l); draw(); }));
  }; draw();
};

/* ============================================================
   TIN TỨC & NỘI DUNG
   ============================================================ */
PAGES.content = v => {
  let tab = 'news';
  const draw = () => {
    if (tab === 'news') {
      const news = getNews();
      $('#ctBody').innerHTML = '<div class="toolbar"><span class="grow"></span><button class="btn btn-p" id="nwAdd"><i class="fas fa-plus"></i> Viết bài mới</button></div>' + (news.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Bài viết</th><th>Chuyên mục</th><th>Ngày</th><th></th></tr>' + news.map((n, i) => '<tr><td><div class="cell"><div class="thumb" style="background:linear-gradient(135deg,' + n.color + ',' + n.color2 + ');display:grid;place-items:center;font-size:1.4rem">' + n.emoji + '</div><div><b>' + esc(n.title) + '</b><small>' + esc(n.author) + '</small></div></div></td><td>' + badge(n.cat, 'p') + '</td><td>' + esc(n.date) + '</td><td class="act"><button class="btn btn-o btn-s" data-edit="' + i + '"><i class="fas fa-pen"></i></button><button class="btn btn-d btn-s" data-del="' + i + '"><i class="fas fa-trash"></i></button></td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-newspaper', 'Chưa có bài viết') + '</div>');
      const form = i => { const n = i == null ? { cat: 'Hướng dẫn', emoji: '🎸', color: '#6C5CE7', color2: '#A29BFE', author: me().name, content: [] } : getNews()[i];
        const m = modal(i == null ? 'Viết bài mới' : 'Sửa bài viết', '<div class="fg"><label>Tiêu đề *</label><input class="input" id="nwTitle" value="' + esc(n.title || '') + '"></div><div class="frow3"><div class="fg"><label>Chuyên mục</label><select class="select" id="nwCat">' + ['Hướng dẫn', 'Tin tức', 'Khuyến mãi', 'Mẹo hay', 'Đánh giá'].concat(n.cat && !['Hướng dẫn', 'Tin tức', 'Khuyến mãi', 'Mẹo hay', 'Đánh giá'].includes(n.cat) ? [n.cat] : []).map(c => '<option' + (c === n.cat ? ' selected' : '') + '>' + c + '</option>').join('') + '</select></div><div class="fg"><label>Emoji</label><input class="input" id="nwEmoji" maxlength="4" value="' + esc(n.emoji) + '"></div><div class="fg"><label>Tác giả</label><input class="input" id="nwAuthor" value="' + esc(n.author) + '"></div></div><div class="frow"><div class="fg"><label>Màu 1</label><input class="input" type="color" id="nwC1" value="' + n.color + '" style="padding:2px;height:40px"></div><div class="fg"><label>Màu 2</label><input class="input" type="color" id="nwC2" value="' + n.color2 + '" style="padding:2px;height:40px"></div></div><div class="fg"><label>Tóm tắt *</label><textarea class="textarea" rows="2" id="nwEx">' + esc(n.excerpt || '') + '</textarea></div><div class="fg"><label>Nội dung (cách nhau bằng 1 dòng trống)</label><textarea class="textarea" rows="8" id="nwBody">' + esc((n.content || []).join('\n\n')) + '</textarea></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="nwSave">Đăng bài</button>', 'lg');
        $('#nwSave', m).onclick = () => { const title = $('#nwTitle', m).value.trim(), ex = $('#nwEx', m).value.trim(), body = $('#nwBody', m).value.split(/\n\s*\n/).map(x => x.trim()).filter(Boolean); if (title.length < 5) return toast('Tiêu đề tối thiểu 5 ký tự', 'err'); if (ex.length < 10) return toast('Nhập tóm tắt bài viết', 'err'); if (!body.length) return toast('Nhập nội dung bài viết', 'err');
          const list = getNews(); const item = Object.assign({}, i == null ? { id: list.reduce((mx, x) => Math.max(mx, x.id), 0) + 1, date: new Date().toLocaleDateString('vi-VN') } : list[i], { title, excerpt: ex, content: body, cat: $('#nwCat', m).value, emoji: $('#nwEmoji', m).value.trim() || '🎵', author: $('#nwAuthor', m).value.trim() || 'Soundora', color: $('#nwC1', m).value, color2: $('#nwC2', m).value });
          if (i == null) list.unshift(item); else list[i] = item; save('a_news', list); log(i == null ? 'Đăng bài' : 'Sửa bài', title); toast('Đã lưu bài viết'); closeModal(); draw(); }; };
      $('#nwAdd').onclick = () => form(null); $$('[data-edit]', v).forEach(b => b.onclick = () => form(+b.dataset.edit));
      $$('[data-del]', v).forEach(b => b.onclick = () => confirmBox('Xóa bài viết này?', () => { const l = getNews(); const t = l.splice(+b.dataset.del, 1)[0]; save('a_news', l); log('Xóa bài', t.title); draw(); }));
    } else {
      const s = load('a_site', { freeShipFrom: SITE.freeShipFrom, shipFee: SITE.shipFee });
      $('#ctBody').innerHTML = '<div class="card" style="max-width:560px"><h3><i class="fas fa-truck"></i> Vận chuyển</h3><div class="frow"><div class="fg"><label>Miễn phí ship từ (đ)</label><input class="input" type="number" min="0" id="stFree" value="' + s.freeShipFrom + '"></div><div class="fg"><label>Phí vận chuyển (đ)</label><input class="input" type="number" min="0" id="stFee" value="' + s.shipFee + '"></div></div><button class="btn btn-p" id="stSave"><i class="fas fa-save"></i> Lưu</button><p class="hint">Áp dụng cho giỏ hàng và thanh toán. Hotline, địa chỉ và banner hiện được sửa trực tiếp trong các file HTML.</p></div>';
      $('#stSave').onclick = () => { save('a_site', { freeShipFrom: Math.max(0, +$('#stFree').value || 0), shipFee: Math.max(0, +$('#stFee').value || 0) }); log('Sửa cài đặt vận chuyển'); toast('Đã lưu'); };
    }
  };
  v.innerHTML = '<div class="tabs"><button data-t="news" class="on">Bài viết</button><button data-t="site">Thông tin website</button></div><div id="ctBody"></div>';
  $$('.tabs button', v).forEach(b => b.onclick = () => { tab = b.dataset.t; $$('.tabs button', v).forEach(x => x.classList.toggle('on', x === b)); draw(); }); draw();
};

/* ============================================================
   TÀI KHOẢN QUẢN TRỊ + NHẬT KÝ
   ============================================================ */
PAGES.staff = v => {
  let tab = 'acc';
  const draw = () => {
    if (tab === 'acc') {
      const list = getStaff();
      $('#sfBody').innerHTML = '<div class="toolbar"><span class="grow"></span><button class="btn btn-o" id="sfPw"><i class="fas fa-key"></i> Đổi mật khẩu của tôi</button><button class="btn btn-p" id="sfAdd"><i class="fas fa-plus"></i> Thêm tài khoản</button></div><div class="tbl-wrap"><table class="tbl"><tr><th>Nhân viên</th><th>Vai trò</th><th>Quyền truy cập</th><th>Trạng thái</th><th></th></tr>' + list.map((s, i) => '<tr><td><b>' + esc(s.name) + '</b><br><small>' + esc(s.email) + '</small></td><td>' + badge(ROLES[s.role].name, 'p') + '</td><td style="max-width:300px"><small>' + (ROLES[s.role].pages === '*' ? 'Toàn quyền' : ROLES[s.role].pages.join(', ')) + '</small></td><td>' + (s.active ? badge('Hoạt động', 'ok') : badge('Vô hiệu', 'bad')) + '</td><td class="act">' + (s.email !== me().email ? '<button class="btn btn-o btn-s" data-tg="' + i + '">' + (s.active ? 'Vô hiệu' : 'Kích hoạt') + '</button><button class="btn btn-d btn-s" data-del="' + i + '"><i class="fas fa-trash"></i></button>' : '<small>(bạn)</small>') + '</td></tr>').join('') + '</table></div>';
      $('#sfAdd').onclick = () => { const m = modal('Thêm tài khoản quản trị', '<div class="frow"><div class="fg"><label>Họ tên *</label><input class="input" id="sfName"></div><div class="fg"><label>Email *</label><input class="input" id="sfEmail" type="email"></div></div><div class="frow"><div class="fg"><label>Mật khẩu * (≥ 6 ký tự)</label><input class="input" id="sfPass" type="password"></div><div class="fg"><label>Vai trò</label><select class="select" id="sfRole">' + Object.keys(ROLES).map(r => '<option value="' + r + '">' + ROLES[r].name + '</option>').join('') + '</select></div></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="sfSave">Tạo</button>', 'sm');
        $('#sfSave', m).onclick = () => { const name = $('#sfName', m).value.trim(), email = $('#sfEmail', m).value.trim().toLowerCase(), pass = $('#sfPass', m).value; if (name.length < 2) return toast('Nhập họ tên', 'err'); if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) return toast('Email không hợp lệ', 'err'); if (pass.length < 6) return toast('Mật khẩu tối thiểu 6 ký tự', 'err'); const l = getStaff(); if (l.some(x => x.email === email)) return toast('Email đã tồn tại', 'err');
          l.push({ name, email, pass: hash(pass), role: $('#sfRole', m).value, active: true, created: new Date().toISOString() }); save('a_staff', l); log('Tạo tài khoản quản trị', email); toast('Đã tạo tài khoản'); closeModal(); draw(); }; };
      $('#sfPw').onclick = () => { const m = modal('Đổi mật khẩu', '<div class="fg"><label>Mật khẩu hiện tại</label><input class="input" id="pwOld" type="password"></div><div class="fg"><label>Mật khẩu mới (≥ 6 ký tự)</label><input class="input" id="pwNew" type="password"></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="pwSave">Đổi</button>', 'sm');
        $('#pwSave', m).onclick = () => { const l = getStaff(); const s = l.find(x => x.email === me().email); if (s.pass !== hash($('#pwOld', m).value)) return toast('Mật khẩu hiện tại không đúng', 'err'); if ($('#pwNew', m).value.length < 6) return toast('Mật khẩu mới tối thiểu 6 ký tự', 'err'); s.pass = hash($('#pwNew', m).value); save('a_staff', l); log('Đổi mật khẩu'); toast('Đã đổi mật khẩu'); closeModal(); }; };
      $$('[data-tg]', v).forEach(b => b.onclick = () => { const l = getStaff(); l[+b.dataset.tg].active = !l[+b.dataset.tg].active; save('a_staff', l); log('Bật/tắt tài khoản QT', l[+b.dataset.tg].email); draw(); });
      $$('[data-del]', v).forEach(b => b.onclick = () => confirmBox('Xóa tài khoản quản trị này?', () => { const l = getStaff(); const t = l.splice(+b.dataset.del, 1)[0]; save('a_staff', l); log('Xóa tài khoản QT', t.email); draw(); }));
    } else {
      const l = load('a_log', []);
      $('#sfBody').innerHTML = l.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Thời gian</th><th>Người thực hiện</th><th>Thao tác</th><th>Chi tiết</th></tr>' + l.slice(0, 150).map(x => '<tr><td>' + dt(x.at) + '</td><td>' + esc(x.by) + '</td><td><b>' + esc(x.action) + '</b></td><td>' + esc(x.detail) + '</td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-clipboard-list', 'Chưa có nhật ký') + '</div>';
    }
  };
  v.innerHTML = '<div class="note"><i class="fas fa-shield-halved"></i> Lưu ý bảo mật: phần đăng nhập Admin hiện chạy hoàn toàn trên trình duyệt (chưa có backend) nên chỉ phù hợp để demo/đồ án. Khi triển khai thật cần xác thực và phân quyền phía máy chủ.</div><div class="tabs"><button data-t="acc" class="on">Tài khoản & vai trò</button><button data-t="log">Nhật ký thao tác</button></div><div id="sfBody"></div>';
  $$('.tabs button', v).forEach(b => b.onclick = () => { tab = b.dataset.t; $$('.tabs button', v).forEach(x => x.classList.toggle('on', x === b)); draw(); }); draw();
};

route();
