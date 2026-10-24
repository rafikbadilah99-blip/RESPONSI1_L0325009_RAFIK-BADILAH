package Resposni1PBO;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Koleksi dinamis ArrayList untuk menyimpan daftar barang
        ArrayList<BarangBantuan> daftarBarang = new ArrayList<>();
        boolean running = true;

        System.out.println("==================================================");
        System.out.println("  SISTEM LOGISTIK BANTUAN PENDIDIKAN DAERAH 3T   ");
        System.out.println("==================================================");

        // Perulangan menu interaktif
        while (running) {
            System.out.println("\n--- MENU PILIHAN ---");
            System.out.println("1. Tambah Barang Bantuan");
            System.out.println("2. Selesai dan Cetak Manifes");
            System.out.print("Pilih menu (1/2): ");

            String pilihan = scanner.nextLine();

            switch (pilihan) {
                case "1":
                    tambahBarang(scanner, daftarBarang);
                    break;
                case "2":
                    if (daftarBarang.isEmpty()) {
                        System.out.println("[!] Belum ada barang yang diinputkan. Silakan input barang terlebih dahulu.");
                    } else {
                        cetakManifes(daftarBarang);
                        running = false; // Menghentikan loop
                    }
                    break;
                default:
                    System.out.println("[!] Pilihan menu tidak valid. Silakan pilih 1 atau 2.");
            }
        }

        scanner.close();
    }

    // Method untuk menambah barang dengan validasi Exception Handling
    private static void tambahBarang(Scanner scanner, ArrayList<BarangBantuan> daftarBarang) {
        boolean inputValid = false;

        while (!inputValid) {
            try {
                System.out.println("\n--- Input Data Barang ---");
                
                System.out.print("Masukkan Kode Barang (diawali EDX-): ");
                String kodeBarang = scanner.nextLine();
                // Validasi kode barang diawali dengan "EDX-"
                if (!kodeBarang.startsWith("EDX-")) {
                    throw new IllegalArgumentException("Kode barang harus diawali EDX-");
                }

                System.out.print("Masukkan Nama Barang: ");
                String namaBarang = scanner.nextLine();

                System.out.print("Masukkan Kategori (contoh: Elektronik, Buku): ");
                String kategori = scanner.nextLine();

                System.out.print("Masukkan Berat Barang (kg): ");
                double beratKg = Double.parseDouble(scanner.nextLine());
                // Validasi berat barang <= 0
                if (beratKg <= 0) {
                    throw new BeratInvalidException("Berat barang harus lebih besar dari 0 kg!");
                }

                // Jika validasi sukses, buat objek dan tambahkan ke ArrayList
                BarangBantuan barang = new BarangBantuan(kodeBarang, namaBarang, kategori, beratKg);
                daftarBarang.add(barang);

                System.out.println("[V] Barang berhasil ditambahkan!");
                inputValid = true; // Keluar dari loop input barang

            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Format berat barang harus angka!");
            } catch (IllegalArgumentException | BeratInvalidException e) {
                System.out.println("[ERROR] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERROR] Terjadi kesalahan input: " + e.getMessage());
            } finally {
                if (!inputValid) {
                    System.out.println("--> Silakan ulangi input data barang dengan benar.");
                }
            }
        }
    }

    // Method untuk menghitung total, menentukan armada, dan mengekspor manifes ke File
    private static void cetakManifes(ArrayList<BarangBantuan> daftarBarang) {
        double totalBerat = 0;

        // Menghitung akumulasi berat[cite: 2]
        for (BarangBantuan b : daftarBarang) {
            totalBerat += b.getBeratKg();
        }

        // Percabangan untuk penentuan jenis armada[cite: 2]
        String jenisArmada;
        if (totalBerat <= 50.0) {
            jenisArmada = "Mobil Pickup";
        } else {
            jenisArmada = "Truk Ekspedisi";
        }

        String namaFile = "manifest_pengiriman.txt";

        // Menuliskan data manifes ke file teks menggunakan BufferedWriter & FileWriter[cite: 2]
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(namaFile))) {
            writer.write("==================================================\n");
            writer.write("             MANIFES PENGIRIMAN LOGISTIK          \n");
            writer.write("==================================================\n\n");

            writer.write(String.format("%-10s | %-25s | %-15s | %-10s\n", "Kode", "Nama Barang", "Kategori", "Berat (kg)"));
            writer.write("-------------------------------------------------------------------\n");

            for (BarangBantuan b : daftarBarang) {
                writer.write(String.format("%-10s | %-25s | %-15s | %-10.2f\n",
                        b.getKodeBarang(),
                        b.getNamaBarang(),
                        b.getKategori(),
                        b.getBeratKg()));
            }

            writer.write("-------------------------------------------------------------------\n");
            writer.write(String.format("Total Jumlah Barang : %d barang\n", daftarBarang.size()));
            writer.write(String.format("Total Berat Muatan : %.2f kg\n", totalBerat));
            writer.write(String.format("Armada Pengiriman  : %s\n", jenisArmada));
            writer.write("==================================================\n");

            System.out.println("\n[SUCCESS] Data manifes berhasil diekspor ke " + namaFile);

        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menulis ke file: " + e.getMessage());
        }
    }
}