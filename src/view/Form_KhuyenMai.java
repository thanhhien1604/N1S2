/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import java.awt.HeadlessException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Voucher;
import model.VoucherCT;
import service.VoucherCTService;
import service.VoucherService;

public class Form_KhuyenMai extends javax.swing.JPanel {

    private final VoucherCTService service = new VoucherCTService();
    private final VoucherService VCservice = new VoucherService();
    private int row = -1;
    private int check;
    private int pages = 1;
    private final int limit = 5;
    private int numberOfPages;
    private int canExecute = 0;

    public Form_KhuyenMai() {
        initComponents();
        this.fillCbb();
        this.fillCbbVoucher();
        this.row = -1;
        this.updateStatus();
    }

    private void fillCbbVoucher() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbVoucher.getModel();
        model.removeAllElements();

        List<Voucher> list = VCservice.selectAll();
        for (Voucher vc : list) {
            model.addElement(vc);
        }
    }

    private void getPages(List<VoucherCT> list) {
        if (list.size() % limit == 0) {
            numberOfPages = list.size() / limit;
        } else {
            numberOfPages = (list.size() / limit) + 1;
        }

        lblPages.setText("1");
    }

    private void fillTable() {
        DefaultTableModel model = (DefaultTableModel) tblVoucherCT.getModel();
        model.setRowCount(0);

        try {
            Voucher vcCbb = (Voucher) cbbVoucher.getSelectedItem();
            List<VoucherCT> listPages = service.selectByKeyWord(vcCbb.getId());
            this.getPages(listPages);

            List<VoucherCT> list = service.searchKeyWord(vcCbb.getId(), pages, limit);
            for (VoucherCT vc : list) {
                model.addRow(new Object[]{
                    vc.getId(),
                    vc.getVc().getMa(),
                    vc.getVc().getNv().getMa(),
                    vc.getVc().getTen(),
                    vc.getNgayBatDau(),
                    vc.getNgayHetHan(),
                    vc.getSoLuong(),
                    vc.getKieuGiam() ? "%" : "VND",
                    vc.getVc().getTrangThai() ? "Đang sử dụng" : "Hết hạn"
                });
            }
        } catch (Exception e) {
        }
    }

    private void getVoucher() {
        this.fillTable();
        this.row = -1;
        this.updateStatus();
    }

    private void filter() {
        DefaultTableModel model = (DefaultTableModel) tblVoucherCT.getModel();
        model.setRowCount(0);

        String trangThai = (String) cbbTrangThai.getSelectedItem();
        Boolean status = trangThai.equals("Đang sử dụng");
        Integer soLuong = null;
        if (!txtSL_loc.getText().trim().isEmpty()) {
            soLuong = Integer.valueOf(txtSL_loc.getText());
        }
        Integer nam = ychNam.getYear();
        Voucher vcCbb = (Voucher) cbbVoucher.getSelectedItem();
        List<VoucherCT> listPages = service.selectByStatus(status, soLuong, nam, vcCbb.getId());
        this.getPages(listPages);

        List<VoucherCT> list = service.filterStatus(status, soLuong, nam, vcCbb.getId(), pages, limit);

        for (VoucherCT vc : list) {
            model.addRow(new Object[]{
                vc.getId(),
                vc.getVc().getNv().getMa(),
                vc.getVc().getTen(),
                vc.getNgayBatDau(),
                vc.getNgayHetHan(),
                vc.getVc().getNgayTao(),
                vc.getSoLuong(),
                vc.getKieuGiam() ? "%" : "VND",
                vc.getVc().getTrangThai() ? "Đang sử dụng" : "Hết hạn"
            });
        }
    }

    private void fillCbb() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbTrangThai.getModel();
        model.removeAllElements();

        List<VoucherCT> listCbb = service.selectAll();
        Set<String> liSet = new HashSet<>();

        for (VoucherCT vc : listCbb) {
            liSet.add(vc.getVc().getTrangThai() ? "Đang sử dụng" : "Hết hạn");
        }

        for (String status : liSet) {
            model.addElement(status);
        }
    }

    private void setDataForm(VoucherCT vcct) {
        txtSoLuong.setText(String.valueOf(vcct.getSoLuong()));
        rdoPhanTram.setSelected(vcct.getKieuGiam());
        rdoVND.setSelected(!vcct.getKieuGiam());
        txtNgayBatDau.setText(String.valueOf(vcct.getNgayBatDau()));
        txtNgayHetHan.setText(String.valueOf(vcct.getNgayHetHan()));
    }

    private void updateStatus() {
        Boolean edit = this.row >= 0;

        btnAdd.setEnabled(!edit);
        btnUpdate.setEnabled(edit);
        btnDelete.setEnabled(edit);
    }

    private void editForm() {
        Integer id = (Integer) tblVoucherCT.getValueAt(row, 0);
        VoucherCT vcct = service.selectById(id);

        this.setDataForm(vcct);
        this.updateStatus();
    }

    private void firstPage() {
        pages = 1;
        if (canExecute == 1) {
            this.filter();
        } else {
            this.fillTable();
        }

        lblPages.setText("1");
    }

    private void prevPage() {
        if (pages > 1) {
            pages--;
            if (canExecute == 1) {
                this.filter();
            } else {
                this.fillTable();
            }

            lblPages.setText("" + pages);
        }
    }

    private void nextPage() {
        if (pages < numberOfPages) {
            pages++;
            if (canExecute == 1) {
                this.filter();
            } else {
                this.fillTable();
            }

            lblPages.setText("" + pages);
        }
    }

    private void lastPage() {
        pages = numberOfPages;
        if (canExecute == 1) {
            this.filter();
        } else {
            this.fillTable();
        }

        lblPages.setText("" + pages);
    }

    private VoucherCT getDataForm() {
        VoucherCT vcct = new VoucherCT();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = format.parse(txtNgayBatDau.getText());
            vcct.setNgayBatDau(new java.sql.Date(date.getTime()));

            date = format.parse(txtNgayHetHan.getText());
            vcct.setNgayHetHan(new java.sql.Date(date.getTime()));
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng ngày tháng!");
        }
        vcct.setSoLuong(Integer.valueOf(txtSoLuong.getText()));
        vcct.setKieuGiam(rdoPhanTram.isSelected());
        Voucher vc = (Voucher) cbbVoucher.getSelectedItem();
        vcct.setId_voucher(vc.getId());

        return vcct;
    }

    private void insert() {
        check = JOptionPane.showConfirmDialog(this, "Xác nhận thêm dữ liệu?");
        if (check != JOptionPane.YES_OPTION) {
            return;
        }

        VoucherCT vcct = this.getDataForm();

        try {
            service.insert(vcct);
            this.fillTable();
            this.clear();
            JOptionPane.showMessageDialog(this, "Thêm dữ liệu thành công!");
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Thêm dữ liệu thất bại!");
        }
    }

    private void update() {
        check = JOptionPane.showConfirmDialog(this, "Xác nhận sửa dữ liệu?");
        if (check != JOptionPane.YES_OPTION) {
            return;
        }

        VoucherCT vcct = this.getDataForm();
        Integer id = (Integer) tblVoucherCT.getValueAt(row, 0);
        vcct.setId(id);

        try {
            service.update(vcct);
            this.fillTable();
            JOptionPane.showMessageDialog(this, "Sửa dữ liệu thành công!");
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Sửa dữ liệu thất bại!");
        }
    }

    private void delete() {
        check = JOptionPane.showConfirmDialog(this, "Xác nhận xóa dữ liệu?");
        if (check != JOptionPane.YES_OPTION) {
            return;
        }

        Integer id = (Integer) tblVoucherCT.getValueAt(row, 0);
        try {
            service.delete(id);
            this.fillTable();
            JOptionPane.showMessageDialog(this, "Xóa dữ liệu thành công!");
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Xóa dữ liệu thất bại!");
        }
        this.lastPage();
    }

    public void clear() {
        txtSoLuong.setText("");
        buttonGroup1.clearSelection();
        txtNgayBatDau.setText("");
        txtNgayHetHan.setText("");
        this.row = -1;
        this.updateStatus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cbbVoucher = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cbbTrangThai = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtSL_loc = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        btnLoc = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNgayBatDau = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtNgayHetHan = new javax.swing.JTextField();
        txtSoLuong = new javax.swing.JTextField();
        rdoPhanTram = new javax.swing.JRadioButton();
        rdoVND = new javax.swing.JRadioButton();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVoucherCT = new javax.swing.JTable();
        btnFirst = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        lblPages = new javax.swing.JLabel();
        ychNam = new com.toedter.calendar.JYearChooser();
        jLabel3 = new javax.swing.JLabel();
        btnAddKM = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(850, 510));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setText("Quản Lý Khuyến Mãi");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(339, 10, -1, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setText("Khuyến Mãi:");
        add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 42, -1, -1));

        cbbVoucher.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cbbVoucher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbVoucherActionPerformed(evt);
            }
        });
        add(cbbVoucher, new org.netbeans.lib.awtextra.AbsoluteConstraints(119, 40, 630, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Lọc:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 75, 67, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Trạng thái:");
        add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 77, 60, -1));

        cbbTrangThai.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        add(cbbTrangThai, new org.netbeans.lib.awtextra.AbsoluteConstraints(147, 73, 130, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Số lượng:");
        add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(303, 77, 58, -1));

        txtSL_loc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        add(txtSL_loc, new org.netbeans.lib.awtextra.AbsoluteConstraints(371, 74, 71, -1));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Năm:");
        add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 77, 36, -1));

        btnLoc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnLoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Filter.png"))); // NOI18N
        btnLoc.setText("Lọc");
        btnLoc.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocActionPerformed(evt);
            }
        });
        add(btnLoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(635, 76, 70, 25));

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Số lượng");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Ngày bắt đầu");

        txtNgayBatDau.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Kiểu giảm giá");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Ngày hết hạn");

        txtNgayHetHan.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        buttonGroup1.add(rdoPhanTram);
        rdoPhanTram.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        rdoPhanTram.setText("%");

        buttonGroup1.add(rdoVND);
        rdoVND.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        rdoVND.setSelected(true);
        rdoVND.setText("VND");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNgayHetHan))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(rdoPhanTram)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rdoVND))
                            .addComponent(txtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNgayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(rdoPhanTram)
                    .addComponent(rdoVND))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtNgayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtNgayHetHan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 400, -1));

        btnAdd.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnAdd.setText("Add");
        btnAdd.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 240, 60, 25));

        btnUpdate.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 240, 60, 25));

        btnDelete.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 240, 60, 25));

        btnNew.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnNew.setText("New");
        btnNew.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 240, 60, 25));

        tblVoucherCT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Mã voucher", "Mã nhân viên", "Tên voucher", "Ngày bắt đầu", "Ngày hết hạn", "Số lượng", "Kiểu giảm", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblVoucherCT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherCTMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblVoucherCT);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, 829, 220));

        btnFirst.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnFirst.setText("<<");
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });
        add(btnFirst, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 520, -1, -1));

        btnBack.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnBack.setText("<");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        add(btnBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 520, -1, -1));

        btnNext.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnNext.setText(">");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        add(btnNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 520, -1, -1));

        btnLast.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnLast.setText(">>");
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });
        add(btnLast, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 520, -1, -1));
        add(lblPages, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 520, 30, 20));
        add(ychNam, new org.netbeans.lib.awtextra.AbsoluteConstraints(524, 73, 70, 25));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Form:");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        btnAddKM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/adds.png"))); // NOI18N
        add(btnAddKM, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 40, 25, 25));
    }// </editor-fold>//GEN-END:initComponents

    private void cbbVoucherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbVoucherActionPerformed
        // TODO add your handling code here:
        this.getVoucher();
    }//GEN-LAST:event_cbbVoucherActionPerformed

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocActionPerformed
        // TODO add your handling code here:
        this.canExecute = 1;
        this.filter();
        this.firstPage();
    }//GEN-LAST:event_btnLocActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        this.insert();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        this.update();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        this.delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:
        this.clear();
    }//GEN-LAST:event_btnNewActionPerformed

    private void tblVoucherCTMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherCTMouseClicked
        // TODO add your handling code here:
        this.row = tblVoucherCT.getSelectedRow();
        this.editForm();
    }//GEN-LAST:event_tblVoucherCTMouseClicked

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        // TODO add your handling code here:
        this.firstPage();
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        this.prevPage();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        this.nextPage();
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        this.lastPage();
    }//GEN-LAST:event_btnLastActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddKM;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbbTrangThai;
    private javax.swing.JComboBox<String> cbbVoucher;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPages;
    private javax.swing.JRadioButton rdoPhanTram;
    private javax.swing.JRadioButton rdoVND;
    private javax.swing.JTable tblVoucherCT;
    private javax.swing.JTextField txtNgayBatDau;
    private javax.swing.JTextField txtNgayHetHan;
    private javax.swing.JTextField txtSL_loc;
    private javax.swing.JTextField txtSoLuong;
    private com.toedter.calendar.JYearChooser ychNam;
    // End of variables declaration//GEN-END:variables
}