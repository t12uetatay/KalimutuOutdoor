package id.t12ue.kalimutu.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bank")
public class BankEntity {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "bankId")
    long bankId;
    @ColumnInfo(name = "bankName")
    String bankName;
    @ColumnInfo(name = "rekeningName")
    String rekeningName;
    @ColumnInfo(name = "rekeningNumber")
    String rekeningNumber;
    @ColumnInfo(name = "arsip")
    boolean arsip;

    public BankEntity(long bankId, String bankName, String rekeningName, String rekeningNumber, boolean arsip){
        this.bankId=bankId;
        this.bankName=bankName;
        this.rekeningName=rekeningName;
        this.rekeningNumber=rekeningNumber;
        this.arsip=arsip;
    }

    public BankEntity(){}

    public long getBankId() {
        return bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public String getRekeningName() {
        return rekeningName;
    }

    public String getRekeningNumber() {
        return rekeningNumber;
    }

    public boolean isArsip() {
        return arsip;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setRekeningName(String rekeningName) {
        this.rekeningName = rekeningName;
    }

    public void setRekeningNumber(String rekeningNumber) {
        this.rekeningNumber = rekeningNumber;
    }

    public void setArsip(boolean arsip) {
        this.arsip = arsip;
    }
}
