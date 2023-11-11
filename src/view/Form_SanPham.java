/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import model.ChatLieu;
import model.DanhMuc;
import model.MauSac;
import model.SanPham;
import model.SanPhamCT;
import model.Size;
import model.ThuongHieu;
import repository.Authu;
import service.ChatLieuService;
import service.DanhMucService;
import service.MauSacService;
import service.SanPhamCTService;
import service.SanPhamService;
import service.SizeService;
import service.ThuongHieuService;

public class Form_SanPham extends javax.swing.JPanel {

    private final SanPhamCTService service = new SanPhamCTService();
    private final SanPhamService spService = new SanPhamService();
    private final SizeService sizeService = new SizeService();
    private final MauSacService msService = new MauSacService();
    private final ChatLieuService clService = new ChatLieuService();
    private final DanhMucService dmService = new DanhMucService();
    private final ThuongHieuService thService = new ThuongHieuService();
    private int row = -1;
    private int pages = 1;
    private final int limit = 5;
    private int numberOfPages;
    private int check;

    public Form_SanPham() {
        initComponents();
        this.fillTable();
        this.loadSearch();
        this.row = -1;
        this.updateStatus();
        this.fillCbbTT();
        this.fillCbbSize();
        this.fillCbbMauSac();
        this.fillCbbChatLieu();
        this.fillCbbDanhMuc();
        this.fillCbbThuongHieu();
        this.loadMa();

    }

    private void getPages(List<SanPhamCT> list) {
        if (list.size() % limit == 0) {
            numberOfPages = list.size() / limit;
        } else {
            numberOfPages = (list.size() / limit) + 1;
        }

        lblPages.setText("1");
    }

    private void fillTable() {
        DefaultTableModel model = (DefaultTableModel) tblSanPhamCT.getModel();
        model.setRowCount(0);

        try {
            String keyword = txtSearch.getText();
            List<SanPhamCT> listPage = service.selectByKeyWord(keyword);
            this.getPages(listPage);

            List<SanPhamCT> list = service.searchKeyWord(keyword, pages, limit);
            for (SanPhamCT spct : list) {
                model.addRow(new Object[]{
                    spct.getId(),
                    spct.getMaSP(),
                    spct.getSanPham().getNhanVien().getMa(),
                    spct.getSanPham().getTen(),
                    spct.getGia(),
                    spct.getSoLuong(),
                    spct.getSize().getTen(),
                    spct.getMauSac().getTen(),
                    spct.getChatLieu().getTen(),
                    spct.getSanPham().getDanhMuc().getTen(),
                    spct.getSanPham().getThuongHieu().getTen(),
                    spct.getTrangThai() ? "Đang bán" : "Ngừng bán"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    private void loadSearch() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fillTable();
                firstPage();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fillTable();
                firstPage();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fillTable();
                firstPage();
            }
        });
    }

    private void loadMa() {
        txtMaSP.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                List<SanPham> list = spService.selectAll();
                for (SanPham sanPham : list) {
                    if (txtMaSP.getText().equalsIgnoreCase(sanPham.getMa())) {
                        txtTenSP.setText(sanPham.getTen());
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                List<SanPham> list = spService.selectAll();
                for (SanPham sanPham : list) {
                    if (txtMaSP.getText().equalsIgnoreCase(sanPham.getMa())) {
                        txtTenSP.setText(sanPham.getTen());
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                List<SanPham> list = spService.selectAll();
                for (SanPham sanPham : list) {
                    if (txtMaSP.getText().equalsIgnoreCase(sanPham.getMa())) {
                        txtTenSP.setText(sanPham.getTen());
                    }
                }
            }
        });
    }

    private void fillCbbTT() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbTrangThai.getModel();
        model.removeAllElements();

        List<SanPhamCT> listCbb = service.selectAll();
        Set<String> liSet = new HashSet<>();

        for (SanPhamCT spct : listCbb) {
            liSet.add(spct.getTrangThai() ? "Đang bán" : "Ngừng bán");
        }

        for (String status : liSet) {
            model.addElement(status);
        }
    }

    private void fillCbbSize() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbSize.getModel();
        model.removeAllElements();

        List<Size> listCbb = sizeService.selectAll();
        for (Size size : listCbb) {
            model.addElement(size);
        }
    }

    private void fillCbbMauSac() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbMauSac.getModel();
        model.removeAllElements();

        List<MauSac> listCbb = msService.selectAll();
        for (MauSac mauSac : listCbb) {
            model.addElement(mauSac);
        }
    }

    private void fillCbbChatLieu() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbChatLieu.getModel();
        model.removeAllElements();

        List<ChatLieu> listCbb = clService.selectAll();
        for (ChatLieu chatLieu : listCbb) {
            model.addElement(chatLieu);
        }
    }

    private void fillCbbDanhMuc() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbDanhMuc.getModel();
        model.removeAllElements();

        List<DanhMuc> listCbb = dmService.selectAll();
        for (DanhMuc danhMuc : listCbb) {
            model.addElement(danhMuc);
        }
    }

    private void fillCbbThuongHieu() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbThuongHieu.getModel();
        model.removeAllElements();

        List<ThuongHieu> listCbb = thService.selectAll();
        for (ThuongHieu th : listCbb) {
            model.addElement(th);
        }
    }

    private void updateStatus() {
        Boolean edit = this.row >= 0;

        btnAdd.setEnabled(!edit);
        btnUpdate.setEnabled(edit);
        btnXoa.setEnabled(edit);
    }

    private void setDataForm(SanPhamCT spct) {
        txtGia.setText(String.valueOf(spct.getGia()));
        txtSoLuong.setText(String.valueOf(spct.getSoLuong()));
        cbbTrangThai.setSelectedItem(spct.getTrangThai() ? "Đang bán" : "Ngừng bán");
    }

    private void editForm() {
        Integer id = (Integer) tblSanPhamCT.getValueAt(row, 0);
        SanPhamCT spct = service.selectById(id);
        this.setDataForm(spct);
        this.updateStatus();
    }

    private void firstPage() {
        pages = 1;
        this.fillTable();
        lblPages.setText("1");
    }

    private void prevPage() {
        if (pages > 1) {
            pages--;
            this.fillTable();
            lblPages.setText("" + pages);
        }
    }

    private void nextPage() {
        if (pages < numberOfPages) {
            pages++;
            this.fillTable();
            lblPages.setText("" + pages);
        }
    }

    private void lastPage() {
        pages = numberOfPages;
        this.fillTable();
        lblPages.setText("" + pages);
    }

    private SanPhamCT getDataForm_spct(List<SanPham> list) {
        SanPhamCT spct = new SanPhamCT();

        spct.setMaSP(txtMaSP.getText());
        for (SanPham sanPham : list) {
            if (txtMaSP.getText().equalsIgnoreCase(sanPham.getMa())) {
                spct.setId_sanPham(sanPham.getId());
            }
        }
        spct.setGia(Double.valueOf(txtGia.getText()));
        spct.setSoLuong(Integer.valueOf(txtSoLuong.getText()));
        String status = (String) cbbTrangThai.getSelectedItem();
        Boolean trangThai = status.equals("Đang bán");
        spct.setTrangThai(trangThai);
        ChatLieu cl = (ChatLieu) cbbChatLieu.getSelectedItem();
        spct.setId_chatLieu(cl.getId());
        Size size = (Size) cbbSize.getSelectedItem();
        spct.setId_size(size.getId());
        MauSac ms = (MauSac) cbbMauSac.getSelectedItem();
        spct.setId_mauSac(ms.getId());

        return spct;
    }

    private SanPham getData_SP() {
        SanPham sp = new SanPham();

        sp.setMa(txtMaSP.getText());
        sp.setTen(txtTenSP.getText());
        sp.setId_nv(Authu.user.getId());
        Date currentDate = new Date();
        sp.setNgayThem(new java.sql.Date(currentDate.getTime()));
        ThuongHieu th = (ThuongHieu) cbbThuongHieu.getSelectedItem();
        sp.setId_th(th.getId());
        DanhMuc dm = (DanhMuc) cbbDanhMuc.getSelectedItem();
        sp.setId_dm(dm.getId());
        return sp;
    }

    private void update() {
        check = JOptionPane.showConfirmDialog(this, "Xác nhận sửa dữ liệu?");
        if (check != JOptionPane.YES_OPTION) {
            return;
        }
        List<SanPham> listSP = spService.selectAll();
        SanPhamCT spct = this.getDataForm_spct(listSP);
        Integer id = (Integer) tblSanPhamCT.getValueAt(row, 0);
        spct.setId(id);
        try {
            service.update(spct);
            this.fillTable();
            JOptionPane.showMessageDialog(this, "Sửa dữ liệu thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    private void delete() {
        check = JOptionPane.showConfirmDialog(this, "Xác nhận xóa dữ liệu?");
        if (check != JOptionPane.YES_OPTION) {
            return;
        }

        Integer id = (Integer) tblSanPhamCT.getValueAt(row, 0);
        try {
            service.delete(id);
            this.fillTable();
            JOptionPane.showMessageDialog(this, "Xóa dữ liệu thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    private void clean() {
        txtGia.setText("");
        txtMaSP.setText("");
        txtSoLuong.setText("");
        txtTenSP.setText("");

        this.row = -1;
        this.updateStatus();
    }

    private void importExcle() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel file", "xls", "xlsx"));
        if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectFile = fileChooser.getSelectedFile();
        String excelFilePath = selectFile.getAbsolutePath();

        List<SanPhamCT> list = service.importExcel(excelFilePath);
        try {
            for (SanPhamCT sanPhamCT : list) {
                service.insert(sanPhamCT);
            }
            fillTable();
            JOptionPane.showMessageDialog(this, "Import dữ liệu thành công! ");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Import dữ liệu thất bại!");
        }
    }

    private void insert_sp_spct() {
        check = JOptionPane.showConfirmDialog(this, "Xác nhận thêm dữ liệu?");
        if (check != JOptionPane.YES_OPTION) {
            return;
        }
        List<SanPham> list = spService.selectAll();
        for (SanPham sanPham : list) {
            if (txtMaSP.getText().trim().equalsIgnoreCase(sanPham.getMa())) {
                List<SanPham> listSP = spService.selectAll();
                SanPhamCT spct = this.getDataForm_spct(listSP);
                try {
                    service.insert(spct);
                    this.fillTable();
                    JOptionPane.showMessageDialog(this, "Thêm dữ liệu thành công!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi truy vấn dữ liệu!");
                }
                return;
            }
        }

        try {
            SanPham sp = this.getData_SP();
            spService.insert(sp);
            List<SanPham> listSP = spService.selectAll();
            SanPhamCT spct = this.getDataForm_spct(listSP);
            service.insert(spct);
            this.fillTable();
            JOptionPane.showMessageDialog(this, "Thêm SP thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn dữ liệu sp!");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSanPhamCT = new javax.swing.JTable();
        btnFirstPages = new javax.swing.JButton();
        btnBackPages = new javax.swing.JButton();
        lblPages = new javax.swing.JLabel();
        btnNextPages = new javax.swing.JButton();
        btnLastPages = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cbbSize = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cbbMauSac = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        cbbChatLieu = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        cbbDanhMuc = new javax.swing.JComboBox<>();
        btnAddSize = new javax.swing.JButton();
        btnMauSac = new javax.swing.JButton();
        btnAddChatLieu = new javax.swing.JButton();
        btnAdđanhMuc = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnMoi = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        btnImport = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        cbbThuongHieu = new javax.swing.JComboBox<>();
        btnAdđanhMuc1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtMaSP = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtTenSP = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtGia = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtSoLuong = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        cbbTrangThai = new javax.swing.JComboBox<>();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setText("Quản Lý Sản Phẩm");

        tblSanPhamCT.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblSanPhamCT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Mã SP", "Mã NV", "Tên SP", "Giá", "Số lượng", "Size", "Màu sắc", "Chất liệu", "Danh mục", "Thương hiệu", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSanPhamCT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSanPhamCTMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblSanPhamCT);

        btnFirstPages.setText("<<");
        btnFirstPages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstPagesActionPerformed(evt);
            }
        });

        btnBackPages.setText("<");
        btnBackPages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackPagesActionPerformed(evt);
            }
        });

        btnNextPages.setText(">");
        btnNextPages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextPagesActionPerformed(evt);
            }
        });

        btnLastPages.setText(">>");
        btnLastPages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastPagesActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Size:");

        cbbSize.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Màu sắc:");

        cbbMauSac.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Chất liệu:");

        cbbChatLieu.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Danh mục:");

        cbbDanhMuc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        btnAddSize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/adds.png"))); // NOI18N
        btnAddSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSizeActionPerformed(evt);
            }
        });

        btnMauSac.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/adds.png"))); // NOI18N
        btnMauSac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMauSacActionPerformed(evt);
            }
        });

        btnAddChatLieu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/adds.png"))); // NOI18N
        btnAddChatLieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddChatLieuActionPerformed(evt);
            }
        });

        btnAdđanhMuc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/adds.png"))); // NOI18N
        btnAdđanhMuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdđanhMucActionPerformed(evt);
            }
        });

        btnAdd.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Add.png"))); // NOI18N
        btnAdd.setText("Thêm");
        btnAdd.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Update.png"))); // NOI18N
        btnUpdate.setText("Sửa");
        btnUpdate.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnXoa.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnXoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Delete.png"))); // NOI18N
        btnXoa.setText("Xóa");
        btnXoa.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnMoi.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnMoi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Clean.png"))); // NOI18N
        btnMoi.setText("Mới");
        btnMoi.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoiActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Tìm kiếm:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("Nhập thông tin:");

        btnImport.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/import.png"))); // NOI18N
        btnImport.setText("Import");
        btnImport.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setText("Thương hiệu");

        btnAdđanhMuc1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/adds.png"))); // NOI18N
        btnAdđanhMuc1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdđanhMuc1ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Mã SP:");

        txtMaSP.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setText("Tên SP:");

        txtTenSP.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel16.setText("Gia:");

        txtGia.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setText("Số lượng:");

        txtSoLuong.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setText("Trạng thái:");

        cbbTrangThai.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(377, 377, 377)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnImport, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel18))
                                .addGap(33, 33, 33)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(txtMaSP, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtTenSP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(txtGia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(txtSoLuong, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(cbbTrangThai, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(28, 28, 28)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(cbbSize, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnAddSize, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cbbChatLieu, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(btnAddChatLieu, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(cbbMauSac, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(10, 10, 10)
                                        .addComponent(btnMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel9))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(cbbThuongHieu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cbbDanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnAdđanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnAdđanhMuc1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(7, 7, 7))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(323, 323, 323)
                .addComponent(btnFirstPages)
                .addGap(10, 10, 10)
                .addComponent(btnBackPages)
                .addGap(10, 10, 10)
                .addComponent(lblPages, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(btnNextPages)
                .addGap(6, 6, 6)
                .addComponent(btnLastPages)
                .addContainerGap(329, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel10))
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(jLabel11)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbbSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(jLabel5)
                        .addComponent(txtMaSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(btnAddSize, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbbMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(btnMauSac, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jLabel7)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbbChatLieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnAddChatLieu, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel8)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(17, 17, 17)
                                .addComponent(jLabel14))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbbDanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(btnAdđanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbbThuongHieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnAdđanhMuc1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtTenSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(txtGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(txtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(cbbTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnImport, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(23, 23, 23)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFirstPages)
                    .addComponent(btnBackPages)
                    .addComponent(lblPages, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNextPages)
                    .addComponent(btnLastPages))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnFirstPagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstPagesActionPerformed
        // TODO add your handling code here:
        this.firstPage();
    }//GEN-LAST:event_btnFirstPagesActionPerformed

    private void btnBackPagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackPagesActionPerformed
        // TODO add your handling code here:
        this.prevPage();
    }//GEN-LAST:event_btnBackPagesActionPerformed

    private void btnNextPagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextPagesActionPerformed
        // TODO add your handling code here:
        this.nextPage();
    }//GEN-LAST:event_btnNextPagesActionPerformed

    private void btnLastPagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastPagesActionPerformed
        // TODO add your handling code here:
        this.lastPage();
    }//GEN-LAST:event_btnLastPagesActionPerformed

    private void tblSanPhamCTMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSanPhamCTMouseClicked
        // TODO add your handling code here:
        this.row = tblSanPhamCT.getSelectedRow();
        this.editForm();
    }//GEN-LAST:event_tblSanPhamCTMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
//        this.add();
        this.insert_sp_spct();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        this.update();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
        this.delete();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        // TODO add your handling code here:
        this.importExcle();
    }//GEN-LAST:event_btnImportActionPerformed

    private void btnMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoiActionPerformed
        // TODO add your handling code here:
        this.clean();
    }//GEN-LAST:event_btnMoiActionPerformed

    private void btnAddSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSizeActionPerformed
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            new SizeJDialog(frame, true).setVisible(true);
            this.fillCbbSize();
        }
    }//GEN-LAST:event_btnAddSizeActionPerformed

    private void btnAdđanhMuc1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdđanhMuc1ActionPerformed
        // TODO add your handling code here:
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            new ThuongHieuJDialog(frame, true).setVisible(true);
            this.fillCbbThuongHieu();
        }
    }//GEN-LAST:event_btnAdđanhMuc1ActionPerformed

    private void btnAddChatLieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddChatLieuActionPerformed
        // TODO add your handling code here:
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            new ChatLieuJDialog(frame, true).setVisible(true);
            this.fillCbbChatLieu();
        }
    }//GEN-LAST:event_btnAddChatLieuActionPerformed

    private void btnMauSacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMauSacActionPerformed
        // TODO add your handling code here:
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            new MauSacJDialog(frame, true).setVisible(true);
            this.fillCbbMauSac();
        }
    }//GEN-LAST:event_btnMauSacActionPerformed

    private void btnAdđanhMucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdđanhMucActionPerformed
        // TODO add your handling code here:
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            new DanhMucJDialog(frame, true).setVisible(true);
            this.fillCbbDanhMuc();
        }
    }//GEN-LAST:event_btnAdđanhMucActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddChatLieu;
    private javax.swing.JButton btnAddSize;
    private javax.swing.JButton btnAdđanhMuc;
    private javax.swing.JButton btnAdđanhMuc1;
    private javax.swing.JButton btnBackPages;
    private javax.swing.JButton btnFirstPages;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnLastPages;
    private javax.swing.JButton btnMauSac;
    private javax.swing.JButton btnMoi;
    private javax.swing.JButton btnNextPages;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cbbChatLieu;
    private javax.swing.JComboBox<String> cbbDanhMuc;
    private javax.swing.JComboBox<String> cbbMauSac;
    private javax.swing.JComboBox<String> cbbSize;
    private javax.swing.JComboBox<String> cbbThuongHieu;
    private javax.swing.JComboBox<String> cbbTrangThai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPages;
    private javax.swing.JTable tblSanPhamCT;
    private javax.swing.JTextField txtGia;
    private javax.swing.JTextField txtMaSP;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSoLuong;
    private javax.swing.JTextField txtTenSP;
    // End of variables declaration//GEN-END:variables

}
