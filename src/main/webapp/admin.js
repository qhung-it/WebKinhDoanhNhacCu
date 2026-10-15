/* Soundora Admin: dữ liệu nghiệp vụ được đọc/ghi qua servlet API. */
   const API = 'admin/api/';
   let SESSION = null;
   let navRenderVersion = 0;
   const me = () => SESSION;

   async function api(path, method, body) {
     const res = await fetch(API + path, {
       method: method || 'GET',
       headers: body ? { 'Content-Type': 'application/json' } : {},
       body: body ? JSON.stringify(body) : undefined,
       credentials: 'same-origin'
     });
     const data = await res.json().catch(() => ({}));
     if (res.status === 401 && path !== 'auth/login') { SESSION = null; route(); throw new Error('401'); }
     if (!res.ok) { toast(data.error || 'Có lỗi xảy ra', 'err'); throw new Error(data.error || 'error'); }
     return data;
   }

const $ = (s, r) => (r || document).querySelector(s);
const $$ = (s, r) => Array.from((r || document).querySelectorAll(s));
const fmt = n => Number(n || 0).toLocaleString('vi-VN') + 'đ';
const esc = s => String(s == null ? '' : s).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
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
function log(action, detail) {
  api('cms/audit', 'POST', { action, detail: detail || '' }).catch(() => {});
}
const can = page => { const u = me(); if (!u) return false; const r = ROLES[u.role]; return r && (r.pages === '*' || r.pages.includes(page)); };
const dt = iso => { try { return new Date(iso).toLocaleString('vi-VN'); } catch (e) { return iso || ''; } };
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
const payBadge = s => badge(s, { 'Ch\u01b0a thanh to\u00e1n': 'warn', '\u0110\u00e3 thanh to\u00e1n': 'ok', '\u0110\u00e3 \u0111\u1eb7t c\u1ecdc': 'info', '\u0110\u00e3 ho\u00e0n ti\u1ec1n': 'info' }[s] || 'mute');
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
  const renderVersion = ++navRenderVersion;
  const u = me();
  const title = (NAV.flatMap(g => g[1]).find(x => x[0] === page) || [0, 0, 'Dashboard'])[2];
  App.innerHTML = '<div class="layout"><aside class="side" id="side"><div class="side-brand"><i class="fas fa-music"></i><div>Soundora<small>ADMIN PANEL</small></div></div><nav class="side-nav">' +
    NAV.map(g => {
      const items = g[1].filter(x => can(x[0]));
      if (!items.length) return '';
      return '<div class="side-group">' + g[0] + '</div>' + items.map(x => {
        return '<a data-nav="' + x[0] + '" href="#/' + x[0] + '" class="' + (x[0] === page ? 'active' : '') + '"><i class="fas ' + x[1] + '"></i>' + x[2] + '</a>';
      }).join('');
    }).join('') +
    '</nav><div class="side-foot"><div class="avatar">' + esc(u.name.charAt(0).toUpperCase()) + '</div><div style="flex:1;min-width:0"><b>' + esc(u.name) + '</b><small>' + esc(ROLES[u.role].name) + '</small></div><button class="btn btn-o btn-s" id="logoutBtn" title="Đăng xuất"><i class="fas fa-sign-out-alt"></i></button></div></aside>' +
    '<div class="main"><header class="top"><button class="burger" id="burger"><i class="fas fa-bars"></i></button><h1>' + title + '</h1><a href="index.html" target="_blank" class="btn btn-o btn-s"><i class="fas fa-external-link-alt"></i> Xem cửa hàng</a></header><div class="content" id="view"></div></div></div>';
  $('#burger').onclick = () => $('#side').classList.toggle('open');
  $('#logoutBtn').onclick = async () => {
    try { await api('auth/logout', 'POST'); } catch (e) {}
    SESSION = null; route();
  };
  $$('.side-nav a').forEach(a => a.onclick = () => $('#side').classList.remove('open'));
  Promise.all([api('orders'), api('cms/contacts')]).then(([orders, contacts]) => {
    if (renderVersion !== navRenderVersion) return;
    const counts = { orders: orders.filter(o => o.status === 'PROCESSING').length, contacts: contacts.filter(c => c.status === 'Mới').length };
    Object.keys(counts).forEach(key => {
      const link = $('[data-nav="' + key + '"]');
      if (!link) return;
      const badges = $$('.cnt', link);
      const countBadge = badges.shift();
      badges.forEach(duplicate => duplicate.remove());
      if (counts[key]) {
        if (countBadge) countBadge.textContent = String(counts[key]);
        else link.insertAdjacentHTML('beforeend', '<span class="cnt">' + counts[key] + '</span>');
      } else if (countBadge) {
        countBadge.remove();
      }
    });
  }).catch(() => {});
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
    '<button class="btn btn-p" style="width:100%;justify-content:center;padding:12px">Đăng nhập</button><div class="hint"><i class="fas fa-info-circle"></i> Đăng nhập bằng tài khoản quản trị trong database.</div><p style="margin:16px 0 0;text-align:center"><a href="index.html" style="color:var(--p)">← Về trang cửa hàng</a></p></form></div>';
  $('#lgForm').onsubmit = async e => {
    e.preventDefault();
    const em = $('#lgEmail').value.trim(), pw = $('#lgPass').value;
    if (!em || !pw) return toast('Nhập email và mật khẩu', 'err');
    try {
      SESSION = await api('auth/login', 'POST', { email: em, password: pw });
      if (location.hash === '#/dashboard') route();
      else location.hash = '#/dashboard';
    } catch (e) { /* api() đã hiện toast */ }
  };
}

/* ============================================================
   DASHBOARD
   ============================================================ */
function dayKey(iso) { const d = new Date(iso); return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0'); }
const revenueOf = list => list.reduce((s, o) => s + (o.totals ? o.totals.total : 0), 0);
PAGES.dashboard = v => {
  v.innerHTML = '<div class="card">' + empty('fa-spinner fa-spin', 'Đang tải tổng quan...') + '</div>';
  Promise.all([api('orders'), api('products'), api('customers')]).then(([orders, products, customers]) => {
    const valid = orders.filter(o => o.status !== 'CANCELLED'), today = dayKey(new Date().toISOString());
    const revenue = list => list.reduce((sum, o) => sum + Number(o.total || 0), 0);
    const days = []; for (let i = 6; i >= 0; i--) { const d = new Date(); d.setDate(d.getDate() - i); days.push(dayKey(d.toISOString())); }
    const rev = days.map(day => revenue(valid.filter(o => dayKey(o.createdAt) === day))), max = Math.max(...rev, 1);
    const sold = {}; valid.forEach(o => (o.items || []).forEach(i => { const key = i.productId || i.name; sold[key] = sold[key] || { name: i.name, qty: 0, rev: 0 }; sold[key].qty += i.qty; sold[key].rev += Number(i.qty) * Number(i.price); }));
    const top = Object.values(sold).sort((a, b) => b.rev - a.rev).slice(0, 5), low = products.filter(p => p.active && p.quantity <= 5);
    const statusName = { PROCESSING: 'Đang xử lý', CONFIRMED: 'Đã xác nhận', SHIPPING: 'Đang giao', COMPLETED: 'Hoàn thành', CANCELLED: 'Đã hủy' };
    const kpi = (ic, col, label, value) => '<div class="card kpi"><div class="ic" style="background:' + col + '"><i class="fas ' + ic + '"></i></div><div><small>' + label + '</small><b>' + value + '</b></div></div>';
    const statusCount = status => orders.filter(o => o.status === status).length;
    v.innerHTML = '<div class="grid g4" style="margin-bottom:18px">' + kpi('fa-sack-dollar','linear-gradient(135deg,#6C5CE7,#A29BFE)','Doanh thu đơn hợp lệ',fmt(revenue(valid))) + kpi('fa-receipt','linear-gradient(135deg,#0984E3,#74B9FF)','Tổng đơn hàng',orders.length + ' <small>(' + orders.filter(o => dayKey(o.createdAt) === today).length + ' hôm nay)</small>') + kpi('fa-users','linear-gradient(135deg,#00B894,#55EFC4)','Khách hàng',customers.length) + kpi('fa-box-open','linear-gradient(135deg,#E17055,#FAB1A0)','Sắp hết / hết hàng',low.length) + '</div>' +
      '<div class="grid g21" style="margin-bottom:18px"><div class="card"><h3><i class="fas fa-chart-column"></i> Doanh thu 7 ngày gần nhất</h3><div class="bars">' + rev.map((r,i) => '<div class="bar"><em>' + (r ? (r >= 1e6 ? (r/1e6).toFixed(1)+'tr' : Math.round(r/1e3)+'k') : '') + '</em><i style="height:'+Math.max(2,r/max*100)+'%"></i><span>'+days[i].slice(8)+'/'+days[i].slice(5,7)+'</span></div>').join('') + '</div></div><div class="card"><h3><i class="fas fa-list-check"></i> Trạng thái đơn</h3>' + Object.keys(statusName).map(s => '<div class="hbar"><span class="nm">'+statusName[s]+'</span><div class="tr"><i style="width:'+(orders.length?statusCount(s)/orders.length*100:0)+'%"></i></div><b>'+statusCount(s)+'</b></div>').join('') + '</div></div>' +
      '<div class="grid g2"><div class="card"><h3><i class="fas fa-trophy"></i> Top sản phẩm theo doanh thu</h3>' + (top.length ? top.map(t => '<div class="hbar"><span class="nm">'+esc(t.name)+'</span><div class="tr"><i style="width:'+t.rev/top[0].rev*100+'%"></i></div><b>'+fmt(t.rev)+'</b></div>').join('') : empty('fa-chart-simple','Chưa có đơn hàng')) + '</div><div class="card"><h3><i class="fas fa-triangle-exclamation"></i> Cảnh báo tồn kho</h3>' + (low.length ? '<table class="tbl">'+low.slice(0,8).map(p=>'<tr><td><b>'+esc(p.name)+'</b><small>'+esc(p.sku)+'</small></td><td class="num">'+(p.quantity?badge('Còn '+p.quantity,'warn'):badge('Hết hàng','bad'))+'</td></tr>').join('')+'</table>' : empty('fa-check-circle','Không có sản phẩm còn 5 hoặc ít hơn')) + '</div></div>';
  }).catch(() => { v.innerHTML = '<div class="card">' + empty('fa-circle-exclamation', 'Không tải được dữ liệu tổng quan') + '</div>'; });
};
/* ============================================================
   THỐNG KÊ
   ============================================================ */
PAGES.stats = v => {
  let mode = 'day', from = '', to = '', orders = [], products = [];
  const dayKey = value => { const d = new Date(value); return d.getFullYear() + '-' + String(d.getMonth()+1).padStart(2,'0') + '-' + String(d.getDate()).padStart(2,'0'); };
  const draw = () => {
    const valid = orders.filter(o => o.status !== 'CANCELLED');
    const inRange = valid.filter(o => { const k=dayKey(o.createdAt); return (!from||k>=from)&&(!to||k<=to); });
    const groups = {}; inRange.forEach(o => { const key=dayKey(o.createdAt), group=mode==='day'?key:mode==='month'?key.slice(0,7):key.slice(0,4); groups[group]=groups[group]||{n:0,r:0}; groups[group].n++; groups[group].r+=Number(o.total||0); });
    const keys=Object.keys(groups).sort().reverse(), productMap=Object.fromEntries(products.map(p=>[p.id,p]));
    const byProd={},byCat={}; inRange.forEach(o=>(o.items||[]).forEach(i=>{const p=productMap[i.productId],category=p?p.categoryName:'Khác';byProd[i.name]=(byProd[i.name]||0)+Number(i.qty)*Number(i.price);byCat[category]=(byCat[category]||0)+Number(i.qty)*Number(i.price);}));
    const rank=o=>Object.entries(o).sort((a,b)=>b[1]-a[1]).slice(0,8), bars=list=>list.length?list.map(x=>'<div class="hbar"><span class="nm">'+esc(x[0])+'</span><div class="tr"><i style="width:'+x[1]/list[0][1]*100+'%"></i></div><b>'+fmt(x[1])+'</b></div>').join(''):empty('fa-chart-simple','Chưa có dữ liệu');
    $('#stBody').innerHTML='<div class="grid g2" style="margin-bottom:18px"><div class="card"><h3>Theo sản phẩm</h3>'+bars(rank(byProd))+'</div><div class="card"><h3>Theo danh mục</h3>'+bars(rank(byCat))+'</div></div><div class="card"><h3>Doanh thu theo '+{day:'ngày',month:'tháng',year:'năm'}[mode]+' <span class="grow" style="flex:1"></span><button class="btn btn-o btn-s" id="stCsv"><i class="fas fa-file-csv"></i> Xuất CSV</button></h3>'+(keys.length?'<div class="tbl-wrap"><table class="tbl"><tr><th>Kỳ</th><th class="num">Số đơn</th><th class="num">Doanh thu</th></tr>'+keys.map(k=>'<tr><td>'+k+'</td><td class="num">'+groups[k].n+'</td><td class="num"><b>'+fmt(groups[k].r)+'</b></td></tr>').join('')+'<tr><td><b>Tổng</b></td><td class="num"><b>'+inRange.length+'</b></td><td class="num"><b>'+fmt(inRange.reduce((s,o)=>s+Number(o.total||0),0))+'</b></td></tr></table></div>':empty('fa-chart-line','Không có đơn hàng trong khoảng này'))+'</div>';
    $('#stCsv').onclick=()=>csvDownload('thong-ke-doanh-thu.csv',[['Kỳ','Số đơn','Doanh thu'],...keys.map(k=>[k,groups[k].n,groups[k].r])]);
  };
  v.innerHTML='<div class="toolbar"><div class="tabs" style="margin:0">'+[['day','Theo ngày'],['month','Theo tháng'],['year','Theo năm']].map(x=>'<button data-m="'+x[0]+'" class="'+(x[0]===mode?'on':'')+'">'+x[1]+'</button>').join('')+'</div><span class="grow"></span><label>Từ</label><input type="date" class="input" id="stFrom" style="max-width:160px"><label>Đến</label><input type="date" class="input" id="stTo" style="max-width:160px"></div><div id="stBody">'+empty('fa-spinner fa-spin','Đang tải thống kê...')+'</div>';
  $$('.tabs button',v).forEach(b=>b.onclick=()=>{mode=b.dataset.m;$$('.tabs button',v).forEach(x=>x.classList.toggle('on',x===b));draw();});
  $('#stFrom').onchange=e=>{from=e.target.value;draw();};$('#stTo').onchange=e=>{to=e.target.value;draw();};
  Promise.all([api('orders'),api('products')]).then(([o,p])=>{orders=o;products=p;draw();}).catch(()=>{$('#stBody').innerHTML=empty('fa-circle-exclamation','Không tải được thống kê');});
};
PAGES.orders = v => {
  let q = '', st = '', page = 1, orders = [];
  const statusName = { PROCESSING: 'Đang xử lý', CONFIRMED: 'Đã xác nhận', SHIPPING: 'Đang giao', DELIVERED: '\u0110\u00e3 giao - ch\u1edd thu', COMPLETED: 'Hoàn thành', CANCELLED: 'Đã hủy' };
  const payMethodName = { CASH: 'Tiền mặt (COD)', E_WALLET: 'Ví điện tử' };
  const payStatusName = { PENDING: 'Ch\u01b0a thanh to\u00e1n', COMPLETED: '\u0110\u00e3 thanh to\u00e1n', DEPOSITED: '\u0110\u00e3 \u0111\u1eb7t c\u1ecdc', FAILED: 'Thanh to\u00e1n th\u1ea5t b\u1ea1i', REFUNDED: '\u0110\u00e3 ho\u00e0n ti\u1ec1n' };
  const reload = async () => { try { orders = await api('orders'); draw(); } catch (e) { $('#odBody').innerHTML = '<div class="card">' + empty('fa-circle-exclamation', 'Không tải được đơn hàng') + '</div>'; } };
  const updateStatus = async (orderId, status) => {
    try { await api('orders/' + encodeURIComponent(orderId) + '/status', 'PATCH', { status }); log('Cập nhật đơn ' + orderId, statusName[status]); toast('Đã cập nhật trạng thái'); await reload(); }
    catch (e) {}
  };
  const detail = order => {
    const next = order.depositAmount > 0
      ? ({ PROCESSING: 'CONFIRMED', SHIPPING: 'DELIVERED' }[order.status])
      : ({ PROCESSING: 'CONFIRMED', CONFIRMED: 'SHIPPING', SHIPPING: 'COMPLETED' }[order.status]);
    const history = order.history || [];
    const body = '<div class="grid g2"><div><h4>Khách hàng</h4><dl class="kv"><dt>Họ tên</dt><dd>' + esc(order.customerName || 'Khách lẻ') + '</dd><dt>Điện thoại</dt><dd>' + esc(order.customerPhone || '-') + '</dd><dt>Email</dt><dd>' + esc(order.customerEmail || '-') + '</dd><dt>Địa chỉ</dt><dd>' + esc(order.address || '-') + '</dd><dt>Ghi chú</dt><dd>' + esc(order.note || '-') + '</dd></dl></div><div><h4>Đơn hàng</h4><dl class="kv"><dt>Mã đơn</dt><dd>#' + esc(order.id) + '</dd><dt>Ngày đặt</dt><dd>' + dt(order.createdAt) + '</dd><dt>Trạng thái</dt><dd>' + orderBadge(statusName[order.status] || order.status) + '</dd><dt>Thanh toán</dt><dd>' + esc(payMethodName[order.payMethod] || order.payMethod || '-') + ' · ' + payBadge(payStatusName[order.payStatus] || order.payStatus || '') + '</dd></dl></div></div>' +
      '<div class="tbl-wrap" style="margin-top:16px"><table class="tbl"><tr><th>Sản phẩm</th><th class="num">Đơn giá</th><th class="num">SL</th><th class="num">Thành tiền</th></tr>' + (order.items || []).map(i => '<tr><td>' + esc(i.name) + '</td><td class="num">' + fmt(i.price) + '</td><td class="num">' + i.qty + '</td><td class="num">' + fmt(Number(i.price) * Number(i.qty)) + '</td></tr>').join('') + '</table></div>' +
      '<dl class="kv" style="margin:14px 0 0 auto;max-width:340px"><dt>Tạm tính</dt><dd class="num">' + fmt(order.subtotal) + '</dd>' + (order.couponCode ? '<dt>Mã giảm giá</dt><dd>' + esc(order.couponCode) + '</dd>' : '') + '<dt>Giảm giá</dt><dd class="num">-' + fmt(order.discount) + '</dd><dt>Vận chuyển</dt><dd class="num">' + fmt(order.shippingFee) + '</dd><dt><b>Tổng cộng</b></dt><dd class="num"><b>' + fmt(order.total) + '</b></dd></dl>' +
      '<h4 style="margin:16px 0 8px">Lịch sử xử lý</h4><div class="timeline">' + history.map(h => '<div><b>' + esc(statusName[h.status] || h.status) + '</b> · ' + dt(h.at) + ' · ' + esc(h.by || '') + '</div>').join('') + '</div>';
    const closed = order.status === 'CANCELLED' || order.status === 'COMPLETED';
    const m = modal('Đơn hàng #' + esc(order.id), body, (closed ? '' : '<button class="btn btn-d" id="odCancel">Hủy đơn</button>') + (next ? '<button class="btn btn-p" id="odNext">' + statusName[next] + '</button>' : '') + '<button class="btn btn-o" data-close>Đóng</button>', 'lg');
    if (next) $('#odNext', m).onclick = async () => { closeModal(); await updateStatus(order.id, next); };
    const cancel = $('#odCancel', m);
    if (cancel) cancel.onclick = () => confirmBox('Hủy đơn #' + order.id + '?', async () => { closeModal(); await updateStatus(order.id, 'CANCELLED'); });
  };
  const draw = () => {
    const list = orders.filter(o => (!st || o.status === st) && (!q || (o.id + ' ' + (o.customerName || '') + ' ' + (o.customerPhone || '')).toLowerCase().includes(q)));
    const pg = pager(list, page, 10);
    $('#odBody').innerHTML = list.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Mã đơn</th><th>Khách hàng</th><th>Ngày</th><th class="num">Tổng tiền</th><th>Thanh toán</th><th>Trạng thái</th><th></th></tr>' + pg.items.map(o => '<tr><td><b>#' + esc(o.id) + '</b></td><td><b>' + esc(o.customerName || 'Khách lẻ') + '</b><small>' + esc(o.customerPhone || '') + '</small></td><td>' + dt(o.createdAt) + '</td><td class="num"><b>' + fmt(o.total) + '</b></td><td>' + payBadge(payStatusName[o.payStatus] || o.payStatus || 'Chưa thanh toán') + '</td><td>' + orderBadge(statusName[o.status] || o.status) + '</td><td class="act"><button class="btn btn-o btn-s" data-view="' + esc(o.id) + '"><i class="fas fa-eye"></i> Chi tiết</button></td></tr>').join('') + '</table></div>' + pagerHTML(pg) : '<div class="card">' + empty('fa-receipt', 'Chưa có đơn hàng trong database') + '</div>';
    $$('[data-view]', v).forEach(b => b.onclick = () => { const order = orders.find(o => o.id === b.dataset.view); if (order) detail(order); });
    $$('[data-pg]', v).forEach(b => b.onclick = () => { page = +b.dataset.pg; draw(); });
  };
  v.innerHTML = '<div class="toolbar"><input class="input" id="odQ" placeholder="Tìm mã đơn, tên, số điện thoại..."><select class="select" id="odSt"><option value="">Tất cả trạng thái</option>' + Object.keys(statusName).map(s => '<option value="' + s + '">' + statusName[s] + '</option>').join('') + '</select><span class="grow"></span><button class="btn btn-o" id="odCsv"><i class="fas fa-file-csv"></i> Xuất CSV</button></div><div id="odBody">' + empty('fa-spinner fa-spin', 'Đang tải đơn hàng...') + '</div>';
  $('#odQ').oninput = e => { q = e.target.value.trim().toLowerCase(); page = 1; draw(); };
  $('#odSt').onchange = e => { st = e.target.value; page = 1; draw(); };
  $('#odCsv').onclick = () => csvDownload('don-hang.csv', [['Mã', 'Ngày', 'Khách', 'SĐT', 'Tổng', 'Thanh toán', 'Trạng thái']].concat(orders.map(o => [o.id, o.createdAt, o.customerName, o.customerPhone, o.total, o.payStatus, o.status])));
  reload();
};
/* ============================================================
   THANH TOÁN
   ============================================================ */
PAGES.payments = v => {
  let st = '', payments = [];
  const statusName = { PENDING: 'Chưa thanh toán', COMPLETED: 'Đã thanh toán', FAILED: 'Thanh toán thất bại', REFUNDED: 'Đã hoàn tiền' };
  const orderStatusName = { PROCESSING: 'Đang xử lý', CONFIRMED: 'Đã xác nhận', SHIPPING: 'Đang giao', DELIVERED: '\u0110\u00e3 giao - ch\u1edd thu', COMPLETED: 'Hoàn thành', CANCELLED: 'Đã hủy' };
  const methodName = { CASH: 'Tiền mặt (COD)', E_WALLET: 'Ví điện tử' };
  const draw = () => {
    const list = payments.filter(p => !st || (st === 'PENDING' ? Number(p.outstandingAmount || 0) > 0 : p.status === st));
    const amountOf = status => status === 'PENDING'
      ? payments.reduce((sum, p) => sum + Number(p.outstandingAmount || 0), 0)
      : payments.filter(p => p.status === status).reduce((sum, p) => sum + Number(p.amount || 0), 0);
    $('#pyBody').innerHTML = '<div class="grid g3" style="margin-bottom:16px"><div class="card kpi"><div class="ic" style="background:var(--ok)"><i class="fas fa-check"></i></div><div><small>Đã thu</small><b>' + fmt(amountOf('COMPLETED')) + '</b></div></div><div class="card kpi"><div class="ic" style="background:var(--warn)"><i class="fas fa-hourglass-half"></i></div><div><small>Chờ thu</small><b>' + fmt(amountOf('PENDING')) + '</b></div></div><div class="card kpi"><div class="ic" style="background:var(--info)"><i class="fas fa-rotate-left"></i></div><div><small>Đã hoàn tiền</small><b>' + fmt(amountOf('REFUNDED')) + '</b></div></div></div>' +
      (list.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Mã giao dịch</th><th>Đơn hàng</th><th>Phương thức</th><th class="num">Số tiền</th><th>Trạng thái</th><th></th></tr>' + list.map(p => '<tr><td>' + esc(p.id || '-') + '</td><td>#' + esc(p.orderId) + ' ' + orderBadge(orderStatusName[p.orderStatus] || p.orderStatus || '') + (p.customerName ? '<small>' + esc(p.customerName) + '</small>' : '') + '</td><td>' + esc(methodName[p.method] || p.method || '-') + '</td><td class="num"><b>' + fmt(p.amount) + '</b>' + (p.deposit ? (p.status === 'COMPLETED' ? '<small>\u0110\u00e3 thu c\u1ecdc ban \u0111\u1ea7u: ' + fmt(p.depositAmount) + '</small>' : '<small>Ti\u1ec1n c\u1ecdc c\u1ea7n thu: ' + fmt(p.depositAmount) + '</small>') : '') + (Number(p.outstandingAmount || 0) > 0 ? '<small>C\u00f2n ph\u1ea3i thu: ' + fmt(p.outstandingAmount) + '</small>' : '') + '</td><td>' + payBadge(p.deposit && p.status === 'COMPLETED' && !p.fullyPaid ? '\u0110\u00e3 \u0111\u1eb7t c\u1ecdc' : (statusName[p.status] || p.status)) + '</td><td class="act">' +
        (p.deposit && p.status === 'PENDING' && p.orderStatus === 'CONFIRMED' ? '<button class="btn btn-ok btn-s" data-paid="' + esc(p.orderId) + '" data-stage="DEPOSIT">X\u00e1c nh\u1eadn thu c\u1ecdc</button>' : (!p.deposit && p.status === 'PENDING' ? '<button class="btn btn-ok btn-s" data-paid="' + esc(p.orderId) + '">X\u00e1c nh\u1eadn \u0111\u00e3 thu</button>' : '')) + (p.deposit && p.status === 'COMPLETED' && p.orderStatus === 'DELIVERED' ? '<button class="btn btn-ok btn-s" data-paid="' + esc(p.orderId) + '" data-stage="REMAINING">X\u00e1c nh\u1eadn thu ph\u1ea7n c\u00f2n l\u1ea1i</button>' : '') + (p.status === 'COMPLETED' ? '<button class="btn btn-d btn-s" data-refund="' + esc(p.orderId) + '">Ho\u00e0n ti\u1ec1n</button>' : '') + '</td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-credit-card', 'Chưa có giao dịch thanh toán') + '</div>');
    $$('[data-paid]', v).forEach(b => b.onclick = async () => updatePayment(b.dataset.paid, 'COMPLETED', b.dataset.stage || ''));
    $$('[data-refund]', v).forEach(b => b.onclick = () => confirmBox('Hoàn tiền đơn #' + b.dataset.refund + '?', async () => updatePayment(b.dataset.refund, 'REFUNDED')));
  };
  const updatePayment = async (orderId, status, stage) => {
    try {
      await api('payments/' + encodeURIComponent(orderId) + '/status', 'PATCH', { status, stage });
      log(status === 'REFUNDED' ? 'Ho\u00e0n ti\u1ec1n' : (stage === 'DEPOSIT' ? 'X\u00e1c nh\u1eadn thu c\u1ecdc' : stage === 'REMAINING' ? 'X\u00e1c nh\u1eadn thu ph\u1ea7n c\u00f2n l\u1ea1i' : 'X\u00e1c nh\u1eadn thu ti\u1ec1n'), orderId);
      toast(status === 'REFUNDED' ? '\u0110\u00e3 ho\u00e0n ti\u1ec1n' : (stage === 'DEPOSIT' ? '\u0110\u00e3 thu c\u1ecdc, \u0111\u01a1n chuy\u1ec3n sang \u0111ang giao' : stage === 'REMAINING' ? '\u0110\u00e3 thu \u0111\u1ee7, \u0111\u01a1n ho\u00e0n t\u1ea5t' : '\u0110\u00e3 x\u00e1c nh\u1eadn thanh to\u00e1n'));
      payments = await api('payments');
      draw();
    } catch (e) { /* api() đã hiển thị lỗi */ }
  };
  v.innerHTML = '<div class="toolbar"><select class="select" id="pySt"><option value="">Tất cả giao dịch</option><option value="PENDING">C&#242;n ph&#7843;i thu</option><option value="COMPLETED">Đã thanh toán</option><option value="FAILED">Thất bại</option><option value="REFUNDED">Đã hoàn tiền</option></select></div><div id="pyBody">' + empty('fa-spinner fa-spin', 'Đang tải giao dịch...') + '</div>';
  $('#pySt').onchange = e => { st = e.target.value; draw(); };
  api('payments').then(data => { payments = Array.isArray(data) ? data : []; draw(); }).catch(() => { $('#pyBody').innerHTML = '<div class="card">' + empty('fa-circle-exclamation', 'Không tải được giao dịch') + '</div>'; });
};
PAGES.products = v => {
  let q = '', cat = '', page = 1, all = [], cats = [], brands = [];
  const thumb = p => '<div class="thumb" style="display:grid;place-items:center">' +
    (p.image ? '<img src="' + esc(p.image) + '" alt="" onerror="this.style.display=\'none\'">' : '<span style="font-size:1.4rem">' + esc(p.emoji || '🎵') + '</span>') + '</div>';
  const BADGE = { hot: ['Bán chạy', 'warn'], new: ['Mới', 'info'], sale: ['Giảm giá', 'bad'] };

  const form = p => {
    const creating = !p;
    p = p || { price: '', quantity: 10, active: true, isNew: false, images: [] };
    const m = modal(creating ? 'Thêm sản phẩm' : 'Sửa sản phẩm',
      '<div class="fg"><label>Tên sản phẩm *</label><input class="input" id="pfName" value="' + esc(p.name || '') + '"></div>' +
      '<div class="frow"><div class="fg"><label>Danh mục *</label><select class="select" id="pfCat">' + cats.map(c => '<option value="' + esc(c.id) + '"' + (c.id === p.categoryId ? ' selected' : '') + '>' + esc(c.name) + '</option>').join('') + '</select></div>' +
      '<div class="fg"><label>Thương hiệu *</label><select class="select" id="pfBrand">' + brands.map(b => '<option value="' + esc(b.id) + '"' + (b.id === p.brandId ? ' selected' : '') + '>' + esc(b.name) + '</option>').join('') + '</select></div></div>' +
      '<div class="frow3"><div class="fg"><label>Giá bán (đ) *</label><input class="input" type="number" min="0" id="pfPrice" value="' + (p.price || '') + '"></div>' +
      '<div class="fg"><label>Giá gốc (đ)</label><input class="input" type="number" min="0" id="pfOld" value="' + (p.oldPrice || '') + '"><small>Để trống nếu không giảm giá</small></div>' +
      '<div class="fg"><label>' + (creating ? 'Tồn kho ban đầu' : 'Tồn kho') + '</label><input class="input" type="number" min="0" id="pfQty" value="' + p.quantity + '"' + (creating ? '' : ' disabled') + '>' + (creating ? '' : '<small>Điều chỉnh tại mục Kho hàng</small>') + '</div></div>' +
      '<div class="frow3"><div class="fg"><label>Nhãn</label><select class="select" id="pfBadge"><option value="">Không</option><option value="hot"' + (p.badge === 'hot' ? ' selected' : '') + '>Bán chạy</option><option value="new"' + (p.badge === 'new' ? ' selected' : '') + '>Mới</option><option value="sale"' + (p.badge === 'sale' ? ' selected' : '') + '>Giảm giá</option></select></div>' +
      '<div class="fg"><label>Emoji dự phòng</label><input class="input" id="pfEmoji" maxlength="4" value="' + esc(p.emoji || '') + '"></div>' +
      '<div class="fg"><label>&nbsp;</label><label class="switch"><input type="checkbox" id="pfActive"' + (p.active !== false ? ' checked' : '') + '> Đang bán</label> <label class="switch" style="margin-left:10px"><input type="checkbox" id="pfNew"' + (p.isNew ? ' checked' : '') + '> Hàng mới</label></div></div>' +
      '<div class="fg"><label>Mô tả ngắn *</label><input class="input" id="pfShort" value="' + esc(p.shortDescription || '') + '"></div>' +
      '<div class="fg"><label>Mô tả chi tiết</label><textarea class="textarea" rows="3" id="pfDesc">' + esc(p.description || '') + '</textarea></div>' +
      '<div class="fg"><label>Ảnh sản phẩm (đường dẫn)</label><div class="img-prev"><img id="pfPrev" alt="" src="' + esc(p.image || '') + '"><div style="flex:1"><input class="input" id="pfImg" placeholder="vd: images/products/f310.jpg" value="' + esc(p.image || '') + '"></div></div></div>' +
      '<div class="fg"><label>Ảnh phụ (mỗi dòng 1 đường dẫn)</label><textarea class="textarea" rows="2" id="pfImgs">' + esc((p.images || []).join('\n')) + '</textarea></div>',
      '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="pfSave"><i class="fas fa-save"></i> Lưu</button>', 'lg');
    $('#pfImg', m).oninput = e => { $('#pfPrev', m).src = e.target.value.trim(); };
    $('#pfSave', m).onclick = async () => {
      const data = {
        name: $('#pfName', m).value.trim(), categoryId: $('#pfCat', m).value, brandId: $('#pfBrand', m).value,
        price: +$('#pfPrice', m).value, oldPrice: +$('#pfOld', m).value || 0, quantity: parseInt($('#pfQty', m).value) || 0,
        badge: $('#pfBadge', m).value, emoji: $('#pfEmoji', m).value.trim(),
        active: $('#pfActive', m).checked, isNew: $('#pfNew', m).checked,
        shortDescription: $('#pfShort', m).value.trim(), description: $('#pfDesc', m).value.trim(),
        image: $('#pfImg', m).value.trim(),
        images: $('#pfImgs', m).value.split('\n').map(x => x.trim()).filter(Boolean)
      };
      if (data.name.length < 3) return toast('Tên sản phẩm tối thiểu 3 ký tự', 'err');
      if (!(data.price > 0)) return toast('Giá bán phải lớn hơn 0', 'err');
      if (data.oldPrice && data.oldPrice <= data.price) return toast('Giá gốc phải lớn hơn giá bán (hoặc để trống)', 'err');
      if (data.shortDescription.length < 5) return toast('Vui lòng nhập mô tả ngắn', 'err');
      if (!data.categoryId || !data.brandId) return toast('Hãy tạo danh mục và thương hiệu trước', 'err');
      try {
        if (creating) await api('products', 'POST', data); else await api('products/' + p.id, 'PUT', data);
        log(creating ? 'Thêm sản phẩm' : 'Sửa sản phẩm', data.name);
        toast('Đã lưu sản phẩm'); closeModal(); reload();
      } catch (e) {}
    };
  };

  const draw = () => {
    const list = all.filter(p => (!cat || p.categoryId === cat) && (!q || (p.name + ' ' + p.brandName + ' ' + (p.sku || '')).toLowerCase().includes(q)));
    const pg = pager(list, page, 10);
    $('#prBody').innerHTML = list.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Sản phẩm</th><th>Danh mục</th><th class="num">Giá</th><th class="num">Tồn</th><th>Trạng thái</th><th></th></tr>' +
      pg.items.map(p => '<tr><td><div class="cell">' + thumb(p) + '<div><b>' + esc(p.name) + '</b><small>' + esc(p.brandName) + ' · ' + esc(p.sku || '') + '</small></div></div></td><td>' + esc(p.categoryName) + '</td>' +
        '<td class="num"><b>' + fmt(p.price) + '</b>' + (p.oldPrice ? '<br><small style="text-decoration:line-through;color:var(--mute)">' + fmt(p.oldPrice) + '</small>' : '') + '</td>' +
        '<td class="num">' + (p.quantity <= 0 ? badge('Hết', 'bad') : p.quantity <= 5 ? badge(p.quantity, 'warn') : p.quantity) + '</td>' +
        '<td>' + (p.active ? badge('Đang bán', 'ok') : badge('Đang ẩn', 'mute')) + (p.badge && BADGE[p.badge] ? ' ' + badge(BADGE[p.badge][0], BADGE[p.badge][1]) : '') + (p.isNew ? ' ' + badge('Hàng mới', 'p') : '') + '</td>' +
        '<td class="act"><button class="btn btn-o btn-s" data-edit="' + esc(p.id) + '"><i class="fas fa-pen"></i></button><button class="btn btn-d btn-s" data-del="' + esc(p.id) + '"><i class="fas fa-trash"></i></button></td></tr>').join('') +
      '</table></div>' + pagerHTML(pg) : '<div class="card">' + empty('fa-guitar', 'Không có sản phẩm phù hợp') + '</div>';
    $$('[data-edit]', v).forEach(b => b.onclick = () => form(all.find(x => x.id === b.dataset.edit)));
    $$('[data-del]', v).forEach(b => b.onclick = () => {
      const p = all.find(x => x.id === b.dataset.del);
      confirmBox('Xóa sản phẩm <b>' + esc(p.name) + '</b>? Nếu chỉ muốn ngừng bán, hãy tắt "Đang bán" khi sửa.', async () => {
        try { await api('products/' + p.id, 'DELETE'); log('Xóa sản phẩm', p.name); toast('Đã xóa'); reload(); } catch (e) {}
      });
    });
    $$('[data-pg]', v).forEach(b => b.onclick = () => { page = +b.dataset.pg; draw(); });
  };

  const reload = async () => {
    try { [all, cats, brands] = await Promise.all([api('products'), api('categories'), api('brands')]); } catch (e) { return; }
    v.innerHTML = '<div class="toolbar"><input class="input" id="prQ" placeholder="Tìm tên, thương hiệu, SKU..." value="' + esc(q) + '"><select class="select" id="prCat"><option value="">Tất cả danh mục</option>' + cats.map(c => '<option value="' + esc(c.id) + '"' + (c.id === cat ? ' selected' : '') + '>' + esc(c.name) + '</option>').join('') + '</select><span class="grow"></span><button class="btn btn-p" id="prAdd"><i class="fas fa-plus"></i> Thêm sản phẩm</button></div><div id="prBody"></div>';
    $('#prQ').oninput = e => { q = e.target.value.trim().toLowerCase(); page = 1; draw(); };
    $('#prCat').onchange = e => { cat = e.target.value; page = 1; draw(); };
    $('#prAdd').onclick = () => form(null);
    draw();
  };
  reload();
};

/* ============================================================
   DANH MỤC & THƯƠNG HIỆU
   ============================================================ */
PAGES.categories = v => {
  const draw = async () => {
    let cats;
    try { cats = await api('categories'); } catch (e) { return; }
    v.innerHTML = '<div class="toolbar"><span class="grow"></span><button class="btn btn-p" id="ctAdd"><i class="fas fa-plus"></i> Thêm danh mục</button></div>' +
      (cats.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Danh mục</th><th class="num">Sản phẩm</th><th>Hiển thị</th><th></th></tr>' +
        cats.map(c => '<tr><td><b>' + esc(c.name) + '</b></td><td class="num">' + c.productCount + '</td><td><label class="switch"><input type="checkbox" data-tg="' + esc(c.id) + '"' + (c.active ? ' checked' : '') + '></label></td><td class="act"><button class="btn btn-o btn-s" data-edit="' + esc(c.id) + '"><i class="fas fa-pen"></i></button><button class="btn btn-d btn-s" data-del="' + esc(c.id) + '"><i class="fas fa-trash"></i></button></td></tr>').join('') +
        '</table></div>' : '<div class="card">' + empty('fa-layer-group', 'Chưa có danh mục') + '</div>');

    const form = c => {
      const isNew = !c;
      const m = modal(isNew ? 'Thêm danh mục' : 'Sửa danh mục',
        '<div class="fg"><label>Tên *</label><input class="input" id="cfName" value="' + esc(c ? c.name : '') + '"></div>',
        '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="cfSave">Lưu</button>', 'sm');
      $('#cfSave', m).onclick = async () => {
        const name = $('#cfName', m).value.trim();
        if (name.length < 2) return toast('Nhập tên danh mục', 'err');
        try {
          if (isNew) await api('categories', 'POST', { name });
          else await api('categories/' + c.id, 'PUT', { name });
          log(isNew ? 'Thêm danh mục' : 'Sửa danh mục', name);
          toast('Đã lưu'); closeModal(); draw();
        } catch (e) {}
      };
    };

    $('#ctAdd').onclick = () => form(null);
    $$('[data-edit]', v).forEach(b => b.onclick = () => form(cats.find(x => x.id === b.dataset.edit)));
    $$('[data-tg]', v).forEach(b => b.onchange = async () => {
      try {
        await api('categories/' + b.dataset.tg + '/status', 'PATCH', { active: b.checked });
        log('Bật/tắt danh mục', b.dataset.tg);
        toast(b.checked ? 'Đã hiện danh mục' : 'Đã ẩn danh mục (sản phẩm thuộc danh mục cũng ẩn)');
      } catch (e) { b.checked = !b.checked; }
    });
    $$('[data-del]', v).forEach(b => b.onclick = () =>
      confirmBox('Xóa danh mục này?', async () => {
        try { await api('categories/' + b.dataset.del, 'DELETE'); log('Xóa danh mục', b.dataset.del); toast('Đã xóa'); draw(); } catch (e) {}
      }));
  };
  draw();
};

PAGES.brands = v => {
  const draw = async () => {
    let brands;
    try { brands = await api('brands'); } catch (e) { return; }
    v.innerHTML = '<div class="toolbar"><span class="grow"></span><button class="btn btn-p" id="brAdd"><i class="fas fa-plus"></i> Thêm thương hiệu</button></div>' +
      (brands.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Thương hiệu</th><th>Xuất xứ</th><th>Mô tả</th><th class="num">Sản phẩm liên kết</th><th></th></tr>' +
        brands.map(b => '<tr><td><b>' + esc(b.name) + '</b></td><td>' + esc(b.origin) + '</td><td style="max-width:320px"><small>' + esc(b.desc) + '</small></td><td class="num">' + b.productCount + '</td><td class="act"><button class="btn btn-o btn-s" data-edit="' + esc(b.id) + '"><i class="fas fa-pen"></i></button><button class="btn btn-d btn-s" data-del="' + esc(b.id) + '"><i class="fas fa-trash"></i></button></td></tr>').join('') +
        '</table></div>' : '<div class="card">' + empty('fa-award', 'Chưa có thương hiệu') + '</div>');

    const form = b => {
      const m = modal(b ? 'Sửa thương hiệu' : 'Thêm thương hiệu',
        '<div class="frow"><div class="fg"><label>Tên *</label><input class="input" id="bfName" value="' + esc(b ? b.name : '') + '"></div><div class="fg"><label>Xuất xứ</label><input class="input" id="bfOrigin" value="' + esc(b ? b.origin : '') + '"></div></div><div class="fg"><label>Mô tả</label><textarea class="textarea" rows="3" id="bfDesc">' + esc(b ? b.desc : '') + '</textarea></div>',
        '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="bfSave">Lưu</button>', 'sm');
      $('#bfSave', m).onclick = async () => {
        const name = $('#bfName', m).value.trim();
        if (name.length < 2) return toast('Nhập tên thương hiệu', 'err');
        const data = { name, origin: $('#bfOrigin', m).value.trim(), desc: $('#bfDesc', m).value.trim() };
        try {
          if (b) await api('brands/' + b.id, 'PUT', data); else await api('brands', 'POST', data);
          log(b ? 'Sửa thương hiệu' : 'Thêm thương hiệu', name);
          toast('Đã lưu'); closeModal(); draw();
        } catch (e) {}
      };
    };

    $('#brAdd').onclick = () => form(null);
    $$('[data-edit]', v).forEach(x => x.onclick = () => form(brands.find(b => b.id === x.dataset.edit)));
    $$('[data-del]', v).forEach(x => x.onclick = () => {
      const b = brands.find(y => y.id === x.dataset.del);
      confirmBox('Xóa thương hiệu <b>' + esc(b.name) + '</b>?', async () => {
        try { await api('brands/' + b.id, 'DELETE'); log('Xóa thương hiệu', b.name); toast('Đã xóa'); draw(); } catch (e) {}
      });
    });
  };
  draw();
};

/* ============================================================
   KHO HÀNG
   ============================================================ */
PAGES.inventory = v => {
  let tab = 'stock', q = '', products = [], history = [];
  const draw = () => {
    const body = $('#ivBody');
    if (tab === 'log') {
      body.innerHTML = history.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Thời gian</th><th>Sản phẩm</th><th>Loại</th><th class="num">Thay đổi</th><th class="num">Trước</th><th class="num">Sau</th><th>Người thực hiện</th><th>Ghi chú</th></tr>' + history.map(h => '<tr><td>' + dt(h.at) + '</td><td><b>' + esc(h.name) + '</b><small>' + esc(h.sku || h.productId) + '</small></td><td>' + esc(h.type) + '</td><td class="num">' + h.qty + '</td><td class="num">' + h.before + '</td><td class="num">' + h.after + '</td><td>' + esc(h.by || 'Hệ thống') + '</td><td>' + esc(h.note || '') + '</td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-clock-rotate-left', 'Chưa có lịch sử nhập xuất kho') + '</div>';
      return;
    }
    const list = products.filter(p => !q || (p.name + ' ' + (p.sku || '')).toLowerCase().includes(q));
    body.innerHTML = list.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Sản phẩm</th><th class="num">Tồn kho</th><th>Tình trạng</th><th></th></tr>' + list.map(p => '<tr><td><div class="cell"><div class="thumb">' + imgTag(p) + '</div><div><b>' + esc(p.name) + '</b><small>' + esc(p.sku) + '</small></div></div></td><td class="num"><b>' + p.quantity + '</b></td><td>' + (p.quantity <= 0 ? badge('Hết hàng', 'bad') : p.quantity <= 5 ? badge('Sắp hết', 'warn') : badge('Ổn định', 'ok')) + '</td><td class="act"><button class="btn btn-ok btn-s" data-stock="' + esc(p.id) + '" data-kind="in">+ Nhập</button><button class="btn btn-o btn-s" data-stock="' + esc(p.id) + '" data-kind="out">- Xuất</button><button class="btn btn-o btn-s" data-stock="' + esc(p.id) + '" data-kind="adj">Điều chỉnh</button></td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-box-open', 'Không có sản phẩm') + '</div>';
    $$('[data-stock]', v).forEach(button => button.onclick = () => {
      const product = products.find(p => p.id === button.dataset.stock), kind = button.dataset.kind;
      const title = { in: 'Nhập kho', out: 'Xuất kho', adj: 'Điều chỉnh tồn kho' }[kind];
      const m = modal(title + ': ' + esc(product.name), '<p style="margin-bottom:12px">Tồn hiện tại: <b>' + product.quantity + '</b></p><div class="fg"><label>' + (kind === 'adj' ? 'Số lượng tồn mới' : 'Số lượng') + '</label><input class="input" type="number" min="0" id="ivQty" value="' + (kind === 'adj' ? product.quantity : 1) + '"></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="ivOk">Xác nhận</button>', 'sm');
      $('#ivOk', m).onclick = async () => {
        const amount = parseInt($('#ivQty', m).value, 10);
        if (!Number.isInteger(amount) || amount < 0) return toast('Số lượng không hợp lệ', 'err');
        const quantity = kind === 'in' ? product.quantity + amount : kind === 'out' ? product.quantity - amount : amount;
        if (quantity < 0) return toast('Không thể xuất quá số lượng tồn', 'err');
        try { await api('products/' + encodeURIComponent(product.id) + '/stock', 'PATCH', { quantity, operation: kind === 'in' ? 'STOCK_IN' : kind === 'out' ? 'STOCK_OUT' : 'ADJUSTMENT', note: title }); log(title, product.name + ' → ' + quantity); toast('Đã cập nhật kho'); closeModal(); await reload(); }
        catch (e) {}
      };
    });
  };
  const reload = async () => { try { [products, history] = await Promise.all([api('products'), api('cms/inventory-history')]); draw(); } catch (e) { $('#ivBody').innerHTML = '<div class="card">' + empty('fa-circle-exclamation', 'Không tải được tồn kho') + '</div>'; } };
  v.innerHTML = '<div class="toolbar"><div class="tabs" style="margin:0"><button data-t="stock" class="on">Tồn kho</button><button data-t="log">Lịch sử nhập / xuất</button></div><input class="input" id="ivQ" placeholder="Tìm sản phẩm..."></div><div id="ivBody">' + empty('fa-spinner fa-spin', 'Đang tải kho...') + '</div>';
  $$('.tabs button', v).forEach(b => b.onclick = () => { tab = b.dataset.t; $$('.tabs button', v).forEach(x => x.classList.toggle('on', x === b)); $('#ivQ').style.display = tab === 'stock' ? '' : 'none'; draw(); });
  $('#ivQ').oninput = e => { q = e.target.value.trim().toLowerCase(); draw(); };
  reload();
};
PAGES.promotions = v => {
  let promotions = [], products = [], ranks = [];
  const form = promotion => {
    const m = modal(promotion ? 'Sửa khuyến mãi' : 'Thêm khuyến mãi',
      '<div class="frow"><div class="fg"><label>Tên khuyến mãi *</label><input class="input" id="pmName" value="' + esc(promotion ? promotion.name : '') + '"></div><div class="fg"><label>Mã giảm giá (tối đa 20 ký tự)</label><input class="input" id="pmCode" maxlength="20" placeholder="VD: SOUNDORA10" value="' + esc(promotion ? promotion.couponCode || '' : '') + '"></div></div>' +
      '<div class="frow"><div class="fg"><label>Loại giảm</label><select class="select" id="pmType"><option value="PERCENTAGE"' + (!promotion || promotion.discountType === 'PERCENTAGE' ? ' selected' : '') + '>Phần trăm</option><option value="FIXED_AMOUNT"' + (promotion && promotion.discountType === 'FIXED_AMOUNT' ? ' selected' : '') + '>Số tiền cố định</option></select></div><div class="fg"><label>Giá trị *</label><input class="input" id="pmValue" type="number" min="1" value="' + esc(promotion ? promotion.discountValue : '10') + '"></div></div>' +
      '<div class="frow"><div class="fg"><label>Bắt đầu</label><input class="input" type="datetime-local" id="pmStart" value="' + esc(promotion && promotion.startAt ? promotion.startAt.slice(0, 16) : '') + '"></div><div class="fg"><label>Kết thúc</label><input class="input" type="datetime-local" id="pmEnd" value="' + esc(promotion && promotion.endAt ? promotion.endAt.slice(0, 16) : '') + '"></div></div>' +
      '<div class="frow"><div class="fg"><label>Sản phẩm áp dụng *</label><select class="select" id="pmProducts" multiple size="6">' + products.map(p => '<option value="' + esc(p.id) + '"' + (promotion && promotion.productIds.includes(p.id) ? ' selected' : '') + '>' + esc(p.name) + '</option>').join('') + '</select><small>Giữ Ctrl để chọn nhiều sản phẩm.</small></div><div class="fg"><label>Hạng thành viên *</label><select class="select" id="pmRanks" multiple size="6">' + ranks.map(r => '<option value="' + esc(r.id) + '"' + (promotion && promotion.rankIds.includes(r.id) ? ' selected' : '') + '>' + esc(r.name) + '</option>').join('') + '</select><small>Giữ Ctrl để chọn nhiều hạng.</small></div></div>',
      '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="pmSave">Lưu</button>', 'lg');
    $('#pmSave', m).onclick = async () => {
      const data = { name: $('#pmName', m).value.trim(), couponCode: $('#pmCode', m).value.trim().toUpperCase(), discountType: $('#pmType', m).value, discountValue: +$('#pmValue', m).value,
        startAt: $('#pmStart', m).value || null, endAt: $('#pmEnd', m).value || null,
        productIds: Array.from($('#pmProducts', m).selectedOptions).map(o => o.value),
        rankIds: Array.from($('#pmRanks', m).selectedOptions).map(o => o.value) };
      if (!data.name || !(data.discountValue > 0) || !data.productIds.length || !data.rankIds.length) return toast('Nhập đủ thông tin, sản phẩm và hạng áp dụng', 'err');
      try {
        if (promotion) await api('promotions/' + encodeURIComponent(promotion.id), 'PUT', data);
        else await api('promotions', 'POST', data);
        log(promotion ? 'Sửa khuyến mãi' : 'Thêm khuyến mãi', data.name); toast('Đã lưu'); closeModal(); await reload();
      } catch (e) {}
    };
  };
  const draw = () => {
    v.innerHTML = '<div class="toolbar"><span class="grow"></span><button class="btn btn-p" id="pmAdd"><i class="fas fa-plus"></i> Thêm khuyến mãi</button></div>' +
      (promotions.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Khuyến mãi</th><th>Mã</th><th>Ưu đãi</th><th>Thời gian</th><th>Sản phẩm</th><th>Hạng</th><th>Trạng thái</th><th></th></tr>' + promotions.map(p => '<tr><td><b>' + esc(p.name) + '</b></td><td>' + esc(p.couponCode || '—') + '</td><td>' + esc(p.discountType === 'PERCENTAGE' ? p.discountValue + '%' : fmt(p.discountValue)) + '</td><td>' + esc((p.startAt || 'Không giới hạn') + ' → ' + (p.endAt || 'Không giới hạn')) + '</td><td>' + p.productIds.length + '</td><td>' + p.rankIds.length + '</td><td>' + badge(p.active ? 'Đang áp dụng' : 'Hết hạn/chưa bắt đầu', p.active ? 'ok' : 'mute') + '</td><td class="act"><button class="btn btn-o btn-s" data-edit="' + esc(p.id) + '"><i class="fas fa-pen"></i></button><button class="btn btn-d btn-s" data-del="' + esc(p.id) + '"><i class="fas fa-trash"></i></button></td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-ticket', 'Chưa có khuyến mãi trong database') + '</div>');
    $('#pmAdd').onclick = () => form(null);
    $$('[data-edit]', v).forEach(b => b.onclick = () => form(promotions.find(p => p.id === b.dataset.edit)));
    $$('[data-del]', v).forEach(b => b.onclick = () => { const p = promotions.find(x => x.id === b.dataset.del); confirmBox('Xóa khuyến mãi <b>' + esc(p.name) + '</b>?', async () => { try { await api('promotions/' + encodeURIComponent(p.id), 'DELETE'); log('Xóa khuyến mãi', p.name); toast('Đã xóa'); await reload(); } catch (e) {} }); });
  };
  const reload = async () => {
    try {
      [promotions, products, ranks] = await Promise.all([
        api('promotions').catch(e => { throw new Error('khuyến mãi: ' + e.message); }),
        api('products').catch(e => { throw new Error('sản phẩm: ' + e.message); }),
        api('ranks').catch(e => { throw new Error('hạng thành viên: ' + e.message); })
      ]);
      promotions = Array.isArray(promotions) ? promotions : [];
      products = Array.isArray(products) ? products : [];
      ranks = Array.isArray(ranks) ? ranks : [];
      draw();
    }
    catch (e) {
      console.error('Không tải được dữ liệu trang khuyến mãi:', e);
      v.innerHTML = '<div class="card">' + empty('fa-circle-exclamation', 'Không tải được dữ liệu khuyến mãi: ' + esc(e.message || 'Lỗi kết nối API')) + '</div>';
    }
  };
  reload();
};
PAGES.customers = v => {
  let q = '', users = [], orders = [], ranks = [];
  const orderStatusName = { PROCESSING: 'Đang xử lý', CONFIRMED: 'Đã xác nhận', SHIPPING: 'Đang giao', COMPLETED: 'Hoàn thành', CANCELLED: 'Đã hủy' };
  const spendFor = user => orders.filter(o => o.customerEmail === user.email && o.status !== 'CANCELLED').reduce((sum, o) => sum + Number(o.total || 0), 0);
  const ordersFor = user => orders.filter(o => o.customerEmail === user.email);
  const profile = user => {
    const history = ordersFor(user), rank = ranks.find(r => r.id === user.rankId);
    const m = modal('Hồ sơ khách hàng', '<dl class="kv"><dt>Họ tên</dt><dd>' + esc(user.name) + '</dd><dt>Email</dt><dd>' + esc(user.email) + '</dd><dt>Điện thoại</dt><dd>' + esc(user.phone || '-') + '</dd><dt>Địa chỉ</dt><dd>' + esc(user.address || '-') + '</dd><dt>Hạng hiện tại</dt><dd>' + esc(rank ? rank.name : 'Chưa xếp hạng') + '</dd><dt>Tổng chi tiêu</dt><dd>' + fmt(spendFor(user)) + '</dd></dl><div class="fg" style="margin-top:16px"><label>Cập nhật hạng</label><select class="select" id="cuRank"><option value="">Chọn hạng...</option>' + ranks.map(r => '<option value="' + esc(r.id) + '"' + (r.id === user.rankId ? ' selected' : '') + '>' + esc(r.name) + '</option>').join('') + '</select></div><h4 style="margin:16px 0 8px">Lịch sử đơn hàng</h4>' + (history.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Mã đơn</th><th>Ngày</th><th class="num">Tổng</th><th>Trạng thái</th></tr>' + history.map(o => '<tr><td>#' + esc(o.id) + '</td><td>' + dt(o.createdAt) + '</td><td class="num">' + fmt(o.total) + '</td><td>' + orderBadge(orderStatusName[o.status] || o.status) + '</td></tr>').join('') + '</table></div>' : '<p>Chưa có đơn hàng.</p>'), '<button class="btn btn-o" data-close>Đóng</button><button class="btn btn-p" id="cuSaveRank">Lưu hạng</button>', 'lg');
    $('#cuSaveRank', m).onclick = async () => { const rankId = $('#cuRank', m).value; if (!rankId) return toast('Chọn hạng thành viên', 'err'); try { await api('customers/' + encodeURIComponent(user.id) + '/rank', 'PATCH', { rankId }); toast('Đã cập nhật hạng'); closeModal(); await reload(); } catch (e) {} };
  };
  const draw = () => {
    const list = users.filter(u => !q || (u.name + ' ' + u.email + ' ' + (u.phone || '')).toLowerCase().includes(q));
    $('#cuBody').innerHTML = list.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Khách hàng</th><th>Hạng</th><th class="num">Số đơn</th><th class="num">Tổng chi tiêu</th><th></th></tr>' + list.map(u => { const rank = ranks.find(r => r.id === u.rankId); return '<tr><td><div class="cell"><div class="avatar" style="width:38px;height:38px">' + esc((u.name || '?').charAt(0).toUpperCase()) + '</div><div><b>' + esc(u.name) + '</b><small>' + esc(u.email) + (u.phone ? ' · ' + esc(u.phone) : '') + '</small></div></div></td><td>' + esc(rank ? rank.name : 'Chưa xếp hạng') + '</td><td class="num">' + ordersFor(u).length + '</td><td class="num"><b>' + fmt(spendFor(u)) + '</b></td><td class="act"><button class="btn btn-o btn-s" data-profile="' + esc(u.id) + '">Hồ sơ</button></td></tr>'; }).join('') + '</table></div>' : '<div class="card">' + empty('fa-users', 'Chưa có khách hàng trong database') + '</div>';
    $$('[data-profile]', v).forEach(b => b.onclick = () => { const user = users.find(u => u.id === b.dataset.profile); if (user) profile(user); });
  };
  const reload = async () => { try { [users, orders, ranks] = await Promise.all([api('customers'), api('orders'), api('ranks')]); draw(); } catch (e) { $('#cuBody').innerHTML = '<div class="card">' + empty('fa-circle-exclamation', 'Không tải được khách hàng') + '</div>'; } };
  v.innerHTML = '<div class="toolbar"><input class="input" id="cuQ" placeholder="Tìm tên, email, số điện thoại..."></div><div id="cuBody">' + empty('fa-spinner fa-spin', 'Đang tải khách hàng...') + '</div>';
  $('#cuQ').oninput = e => { q = e.target.value.trim().toLowerCase(); draw(); };
  reload();
};
PAGES.tiers = v => {
  let tiers = [], users = [];
  const names = { BRONZE: 'Đồng', SILVER: 'Bạc', GOLD: 'Vàng', VIP: 'VIP' };
  const draw = () => {
    v.innerHTML = '<div class="note"><i class="fas fa-info-circle"></i> Ngưỡng chi tiêu được lưu trong database. Ưu đãi phần trăm chưa có trường tương ứng trong entity CustomerRank.</div><div class="grid g3">' +
      tiers.map(t => '<div class="card"><h3><span class="pill-lg"><i class="fas fa-crown"></i> ' + esc(names[t.name] || t.name) + '</span></h3><dl class="kv"><dt>Chi tiêu tối thiểu</dt><dd><b>' + fmt(t.minSpending) + '</b></dd><dt>Số khách đang thuộc hạng</dt><dd>' + users.filter(u => u.rankId === t.id).length + '</dd></dl><div style="margin-top:14px"><button class="btn btn-o btn-s" data-edit="' + esc(t.id) + '"><i class="fas fa-pen"></i> Sửa ngưỡng</button></div></div>').join('') + '</div>';
    $$('[data-edit]', v).forEach(b => b.onclick = () => { const tier = tiers.find(t => t.id === b.dataset.edit); const m = modal('Sửa ngưỡng hạng ' + esc(names[tier.name] || tier.name), '<div class="fg"><label>Chi tiêu tối thiểu (đ)</label><input class="input" type="number" min="0" id="tiMin" value="' + esc(tier.minSpending) + '"></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="tiSave">Lưu</button>', 'sm'); $('#tiSave', m).onclick = async () => { const minSpending = +$('#tiMin', m).value; if (minSpending < 0) return toast('Ngưỡng không được âm', 'err'); try { await api('ranks/' + encodeURIComponent(tier.id), 'PUT', { name: tier.name, minSpending }); log('Sửa ngưỡng hạng', tier.name); toast('Đã lưu'); closeModal(); await reload(); } catch (e) {} }; });
  };
  const reload = async () => { try { [tiers, users] = await Promise.all([api('ranks'), api('customers')]); draw(); } catch (e) { v.innerHTML = '<div class="card">' + empty('fa-circle-exclamation', 'Không tải được hạng thành viên') + '</div>'; } };
  v.innerHTML = '<div id="tierBody">' + empty('fa-spinner fa-spin', 'Đang tải hạng...') + '</div>';
  reload();
};
PAGES.reviews = v => {
  let status = '', reviews = [];
  const draw = () => {
    const list = reviews.filter(r => !status || (status === 'approved' ? r.approved : !r.approved));
    $('#rvBody').innerHTML = list.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Sản phẩm</th><th>Khách hàng</th><th>Đánh giá</th><th>Nội dung</th><th>Trạng thái</th><th></th></tr>' + list.map(r => '<tr><td><b>' + esc(r.productName) + '</b></td><td><b>' + esc(r.userName) + '</b>' + (r.verified ? '<br>' + badge('Đã mua hàng', 'ok') : '') + '</td><td style="color:#F39C12;white-space:nowrap">' + '★'.repeat(r.rating) + '☆'.repeat(5 - r.rating) + '</td><td style="max-width:320px">' + esc(r.comment) + '</td><td>' + (r.approved ? badge('Đã duyệt', 'ok') : badge('Chờ duyệt', 'warn')) + '</td><td class="act"><button class="btn btn-o btn-s" data-approve="' + esc(r.id) + '" data-next="' + (!r.approved) + '">' + (r.approved ? 'Ẩn' : 'Duyệt') + '</button><button class="btn btn-d btn-s" data-del="' + esc(r.id) + '"><i class="fas fa-trash"></i></button></td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-star', 'Chưa có đánh giá trong database') + '</div>';
    $$('[data-approve]', v).forEach(b => b.onclick = async () => { try { await api('reviews/' + encodeURIComponent(b.dataset.approve) + '/approval', 'PATCH', { approved: b.dataset.next === 'true' }); log(b.dataset.next === 'true' ? 'Duyệt đánh giá' : 'Ẩn đánh giá', b.dataset.approve); toast('Đã cập nhật'); await reload(); } catch (e) {} });
    $$('[data-del]', v).forEach(b => b.onclick = () => confirmBox('Xóa đánh giá này vĩnh viễn?', async () => { try { await api('reviews/' + encodeURIComponent(b.dataset.del), 'DELETE'); log('Xóa đánh giá', b.dataset.del); toast('Đã xóa'); await reload(); } catch (e) {} }));
  };
  const reload = async () => { try { reviews = await api('reviews'); draw(); } catch (e) { $('#rvBody').innerHTML = '<div class="card">' + empty('fa-circle-exclamation', 'Không tải được đánh giá') + '</div>'; } };
  v.innerHTML = '<div class="toolbar"><select class="select" id="rvSt"><option value="">Tất cả</option><option value="pending">Chờ duyệt</option><option value="approved">Đã duyệt</option></select></div><div id="rvBody">' + empty('fa-spinner fa-spin', 'Đang tải đánh giá...') + '</div>';
  $('#rvSt').onchange = e => { status = e.target.value; draw(); };
  reload();
};
PAGES.contacts = v => {
  const SUB = { 'tu-van': 'Tư vấn sản phẩm', 'bao-hanh': 'Bảo hành', 'doi-tra': 'Đổi trả', 'gop-y': 'Góp ý', 'khac': 'Khác' };
  let contacts = [];
  const draw = () => {
    v.innerHTML = contacts.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Người gửi</th><th>Chủ đề</th><th>Nội dung</th><th>Trạng thái</th><th></th></tr>' + contacts.map(c => '<tr><td><b>' + esc(c.name) + '</b><br><small>' + esc(c.email) + ' · ' + esc(c.phone) + '</small><br><small>' + dt(c.date) + '</small></td><td>' + esc(SUB[c.subject] || c.subject) + '</td><td style="max-width:320px">' + esc(c.msg) + (c.note ? '<br><small style="color:var(--p)"><i class="fas fa-note-sticky"></i> ' + esc(c.note) + '</small>' : '') + '</td><td>' + badge(c.status, { 'Mới': 'warn', 'Đang xử lý': 'info', 'Đã xử lý': 'ok' }[c.status] || 'mute') + '</td><td class="act"><button class="btn btn-o btn-s" data-h="' + esc(c.id) + '">Xử lý</button><button class="btn btn-d btn-s" data-del="' + esc(c.id) + '"><i class="fas fa-trash"></i></button></td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-envelope-open', 'Chưa có tin nhắn nào từ khách hàng') + '</div>';
    $$('[data-h]', v).forEach(b => b.onclick = () => { const c = contacts.find(x => x.id === b.dataset.h); if (!c) return;
      const m = modal('Xử lý liên hệ', '<p style="margin-bottom:12px"><b>' + esc(c.name) + '</b> · ' + esc(c.phone) + '</p><p style="background:var(--bg);padding:12px;border-radius:10px;margin-bottom:14px">' + esc(c.msg) + '</p><div class="fg"><label>Trạng thái</label><select class="select" id="ctSt">' + ['Mới', 'Đang xử lý', 'Đã xử lý'].map(s => '<option' + (s === c.status ? ' selected' : '') + '>' + s + '</option>').join('') + '</select></div><div class="fg"><label>Ghi chú nội bộ</label><textarea class="textarea" rows="3" id="ctNote">' + esc(c.note || '') + '</textarea></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="ctSave">Lưu</button>', 'sm');
      $('#ctSave', m).onclick = async () => { try { await api('cms/contacts/' + encodeURIComponent(c.id), 'PATCH', { status: $('#ctSt', m).value, note: $('#ctNote', m).value.trim() }); log('Xử lý liên hệ', c.name); toast('Đã lưu'); closeModal(); await reload(); } catch (e) {} }; });
    $$('[data-del]', v).forEach(b => b.onclick = () => confirmBox('Xóa tin nhắn này?', async () => { try { await api('cms/contacts/' + encodeURIComponent(b.dataset.del), 'DELETE'); log('Xóa liên hệ', b.dataset.del); await reload(); } catch (e) {} }));
  };
  const reload = async () => { try { contacts = await api('cms/contacts'); draw(); } catch (e) { v.innerHTML = '<div class="card">' + empty('fa-circle-exclamation', 'Không tải được liên hệ từ database') + '</div>'; } };
  reload();
};

/* ============================================================
   TIN TỨC & NỘI DUNG
   ============================================================ */
PAGES.content = v => {
  let tab = 'news', articles = [];
  const draw = () => {
    if (tab === 'news') {
      $('#ctBody').innerHTML = '<div class="toolbar"><span class="grow"></span><button class="btn btn-p" id="nwAdd"><i class="fas fa-plus"></i> Viết bài mới</button></div>' + (articles.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Bài viết</th><th>Chuyên mục</th><th>Ngày</th><th></th></tr>' + articles.map(n => '<tr><td><div class="cell"><div class="thumb" style="background:linear-gradient(135deg,' + esc(n.color || '#6C5CE7') + ',' + esc(n.color2 || '#A29BFE') + ');display:grid;place-items:center;font-size:1.4rem">' + esc(n.emoji || '🎵') + '</div><div><b>' + esc(n.title) + '</b><small>' + esc(n.author) + '</small></div></div></td><td>' + badge(n.cat || 'Tin tức', 'p') + '</td><td>' + dt(n.date) + '</td><td class="act"><button class="btn btn-o btn-s" data-edit="' + esc(n.id) + '"><i class="fas fa-pen"></i></button><button class="btn btn-d btn-s" data-del="' + esc(n.id) + '"><i class="fas fa-trash"></i></button></td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-newspaper', 'Chưa có bài viết trong database') + '</div>');
      const form = id => { const n = id == null ? { cat: 'Hướng dẫn', emoji: '🎸', color: '#6C5CE7', color2: '#A29BFE', author: me().name, content: [] } : articles.find(a => a.id === id);
        if (!n) return;
        const m = modal(id == null ? 'Viết bài mới' : 'Sửa bài viết', '<div class="fg"><label>Tiêu đề *</label><input class="input" id="nwTitle" value="' + esc(n.title || '') + '"></div><div class="frow3"><div class="fg"><label>Chuyên mục</label><select class="select" id="nwCat">' + ['Hướng dẫn', 'Tin tức', 'Khuyến mãi', 'Mẹo hay', 'Đánh giá'].concat(n.cat && !['Hướng dẫn', 'Tin tức', 'Khuyến mãi', 'Mẹo hay', 'Đánh giá'].includes(n.cat) ? [n.cat] : []).map(c => '<option' + (c === n.cat ? ' selected' : '') + '>' + c + '</option>').join('') + '</select></div><div class="fg"><label>Emoji</label><input class="input" id="nwEmoji" maxlength="4" value="' + esc(n.emoji) + '"></div><div class="fg"><label>Tác giả</label><input class="input" id="nwAuthor" value="' + esc(n.author) + '"></div></div><div class="frow"><div class="fg"><label>Màu 1</label><input class="input" type="color" id="nwC1" value="' + n.color + '" style="padding:2px;height:40px"></div><div class="fg"><label>Màu 2</label><input class="input" type="color" id="nwC2" value="' + n.color2 + '" style="padding:2px;height:40px"></div></div><div class="fg"><label>Tóm tắt *</label><textarea class="textarea" rows="2" id="nwEx">' + esc(n.excerpt || '') + '</textarea></div><div class="fg"><label>Nội dung (cách nhau bằng 1 dòng trống)</label><textarea class="textarea" rows="8" id="nwBody">' + esc((n.content || []).join('\n\n')) + '</textarea></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="nwSave">Đăng bài</button>', 'lg');
        $('#nwSave', m).onclick = async () => { const title = $('#nwTitle', m).value.trim(), ex = $('#nwEx', m).value.trim(), body = $('#nwBody', m).value.split(/\n\s*\n/).map(x => x.trim()).filter(Boolean); if (title.length < 5) return toast('Tiêu đề tối thiểu 5 ký tự', 'err'); if (ex.length < 10) return toast('Nhập tóm tắt bài viết', 'err'); if (!body.length) return toast('Nhập nội dung bài viết', 'err');
          const payload = { title, excerpt: ex, content: body, cat: $('#nwCat', m).value, emoji: $('#nwEmoji', m).value.trim() || '🎵', author: $('#nwAuthor', m).value.trim() || 'Soundora', color: $('#nwC1', m).value, color2: $('#nwC2', m).value, published: true };
          try { await api(id == null ? 'cms/news' : 'cms/news/' + encodeURIComponent(id), id == null ? 'POST' : 'PUT', payload); log(id == null ? 'Đăng bài' : 'Sửa bài', title); toast('Đã lưu bài viết'); closeModal(); await reloadNews(); } catch (e) {} }; };
      $('#nwAdd').onclick = () => form(null); $$('[data-edit]', v).forEach(b => b.onclick = () => form(b.dataset.edit));
      $$('[data-del]', v).forEach(b => b.onclick = () => confirmBox('Xóa bài viết này?', async () => { const item = articles.find(n => n.id === b.dataset.del); try { await api('cms/news/' + encodeURIComponent(b.dataset.del), 'DELETE'); log('Xóa bài', item ? item.title : b.dataset.del); await reloadNews(); } catch (e) {} }));
    } else {
      const s = siteSettings;
      $('#ctBody').innerHTML = '<div class="card" style="max-width:560px"><h3><i class="fas fa-truck"></i> Vận chuyển</h3><div class="frow"><div class="fg"><label>Miễn phí ship từ (đ)</label><input class="input" type="number" min="0" id="stFree" value="' + s.freeShipFrom + '"></div><div class="fg"><label>Phí vận chuyển (đ)</label><input class="input" type="number" min="0" id="stFee" value="' + s.shipFee + '"></div></div><button class="btn btn-p" id="stSave"><i class="fas fa-save"></i> Lưu</button><p class="hint">Áp dụng cho giỏ hàng và thanh toán. Hotline, địa chỉ và banner hiện được sửa trực tiếp trong các file HTML.</p></div>';
      $('#stSave').onclick = async () => { const payload = { freeShipFrom: Math.max(0, +$('#stFree').value || 0), shipFee: Math.max(0, +$('#stFee').value || 0) }; try { siteSettings = await api('cms/settings', 'PUT', payload); log('Sửa cài đặt vận chuyển'); toast('Đã lưu'); } catch (e) {} };
    }
  };
  v.innerHTML = '<div class="tabs"><button data-t="news" class="on">Bài viết</button><button data-t="site">Thông tin website</button></div><div id="ctBody"></div>';
  let siteSettings = { freeShipFrom: 0, shipFee: 0 };
  const reloadNews = async () => { try { articles = await api('cms/news'); if (tab === 'news') draw(); } catch (e) { $('#ctBody').innerHTML = empty('fa-circle-exclamation', 'Không tải được bài viết từ database'); } };
  const loadSettings = async () => { try { siteSettings = await api('cms/settings'); if (tab === 'site') draw(); } catch (e) { $('#ctBody').innerHTML = empty('fa-circle-exclamation', 'Không tải được cấu hình website'); } };
  $$('.tabs button', v).forEach(b => b.onclick = async () => { tab = b.dataset.t; $$('.tabs button', v).forEach(x => x.classList.toggle('on', x === b)); if (tab === 'news') await reloadNews(); else await loadSettings(); });
  reloadNews();
};

/* ============================================================
   TÀI KHOẢN QUẢN TRỊ + NHẬT KÝ
   ============================================================ */
PAGES.staff = v => {
  let tab = 'acc', users = [], auditEntries = [];
  const draw = () => {
    if (tab === 'acc') {
      $('#sfBody').innerHTML = '<div class="toolbar"><span class="grow"></span><button class="btn btn-o" id="sfPw"><i class="fas fa-key"></i> Đổi mật khẩu của tôi</button><button class="btn btn-p" id="sfAdd"><i class="fas fa-plus"></i> Thêm quản trị viên</button></div>' + (users.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Quản trị viên</th><th>Điện thoại</th><th>Quyền</th></tr>' + users.map(u => '<tr><td><b>' + esc(u.name) + '</b><small>' + esc(u.email) + '</small></td><td>' + esc(u.phone) + '</td><td>' + badge('Quản trị viên', 'p') + (u.email === me().email ? ' (bạn)' : '') + '</td></tr>').join('') + '</table></div>' : '<div class="card">' + empty('fa-users', 'Chưa có tài khoản quản trị') + '</div>');
      $('#sfAdd').onclick = () => {
        const m = modal('Thêm quản trị viên', '<div class="frow"><div class="fg"><label>Họ tên *</label><input class="input" id="sfName"></div><div class="fg"><label>Email *</label><input class="input" id="sfEmail" type="email"></div></div><div class="frow"><div class="fg"><label>Điện thoại *</label><input class="input" id="sfPhone"></div><div class="fg"><label>Mật khẩu * (tối thiểu 6 ký tự)</label><input class="input" id="sfPass" type="password"></div></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="sfSave">Tạo</button>', 'sm');
        $('#sfSave', m).onclick = async () => { const data = { name: $('#sfName', m).value.trim(), email: $('#sfEmail', m).value.trim(), phone: $('#sfPhone', m).value.trim(), password: $('#sfPass', m).value }; try { await api('staff', 'POST', data); log('Tạo tài khoản quản trị', data.email); toast('Đã tạo tài khoản'); closeModal(); await reload(); } catch (e) {} };
      };
      $('#sfPw').onclick = () => {
        const m = modal('Đổi mật khẩu', '<div class="fg"><label>Mật khẩu hiện tại</label><input class="input" id="pwOld" type="password"></div><div class="fg"><label>Mật khẩu mới (tối thiểu 6 ký tự)</label><input class="input" id="pwNew" type="password"></div>', '<button class="btn btn-o" data-close>Hủy</button><button class="btn btn-p" id="pwSave">Đổi</button>', 'sm');
        $('#pwSave', m).onclick = async () => { try { await api('auth/password', 'PATCH', { currentPassword: $('#pwOld', m).value, newPassword: $('#pwNew', m).value }); log('Đổi mật khẩu'); toast('Đã đổi mật khẩu'); closeModal(); } catch (e) {} };
      };
    } else {
      $('#sfBody').innerHTML = auditEntries.length ? '<div class="tbl-wrap"><table class="tbl"><tr><th>Thời gian</th><th>Người thực hiện</th><th>Thao tác</th><th>Chi tiết</th></tr>' + auditEntries.map(x=>'<tr><td>'+dt(x.at)+'</td><td>'+esc(x.by)+'</td><td><b>'+esc(x.action)+'</b></td><td>'+esc(x.detail)+'</td></tr>').join('')+'</table></div>' : '<div class="card">'+empty('fa-clipboard-list','Chưa có nhật ký thao tác trong database')+'</div>';
    }
  };
  const reload = async () => { try { if (tab === 'log') auditEntries = await api('cms/audit'); else users = await api('staff'); draw(); } catch (e) { $('#sfBody').innerHTML = '<div class="card">' + empty('fa-circle-exclamation', tab === 'log' ? 'Không tải được nhật ký thao tác' : 'Không tải được tài khoản quản trị') + '</div>'; } };
  v.innerHTML = '<div class="note"><i class="fas fa-info-circle"></i> Vai trò hiện được database hỗ trợ là USER và ADMIN; trang này chỉ quản lý tài khoản ADMIN.</div><div class="tabs"><button data-t="acc" class="on">Tài khoản quản trị</button><button data-t="log">Nhật ký thao tác</button></div><div id="sfBody">' + empty('fa-spinner fa-spin', 'Đang tải tài khoản...') + '</div>';
  $$('.tabs button',v).forEach(b=>b.onclick=async()=>{tab=b.dataset.t;$$('.tabs button',v).forEach(x=>x.classList.toggle('on',x===b));await reload();});
  reload();
};
fetch(API + 'auth', { credentials: 'same-origin' })
  .then(r => r.ok ? r.json() : null)
  .then(s => { SESSION = s; route(); })
  .catch(() => { SESSION = null; route(); });
