package com.dhanifudin.cashflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dhanifudin.cashflow.adapters.TransactionAdapter;
import com.dhanifudin.cashflow.models.Account;
import com.dhanifudin.cashflow.models.Session;
import com.dhanifudin.cashflow.models.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements TransactionAdapter.OnItemTransactionListener {

    public static final String TRANSACTION_KEY = "TRANSACTION";
    public static final String INDEX_KEY = "INDEX";
    public static final int INSERT_REQUEST = 1;
    public static final int UPDATE_REQUEST = 2;

    private TextView welcomeText;
    private TextView balanceText;
    private RecyclerView transactionsView;
    private TransactionAdapter adapter;
    private Account account;
    private Session session;

    // format tampilan untuk nilai uang
    Locale localeID = new Locale("in", "ID");
    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = Application.getSession();

        welcomeText = findViewById(R.id.text_welcome);
        balanceText = findViewById(R.id.text_balance);
        transactionsView = findViewById(R.id.rv_transactions);

        if (!session.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SaveActivity.class);
                intent.putExtra(TRANSACTION_KEY, new Transaction());
                startActivityForResult(intent, INSERT_REQUEST);
                // TODO: Tambahkan event click fab di sini
            }
        });
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int index = viewHolder.getAdapterPosition();
                account.removeTransaction(index);
                adapter.notifyDataSetChanged();
                balanceText.setText(formatRupiah.format(account.getBalance()));
            }
        };
        new ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(transactionsView);

        account = Application.getAccount();
        welcomeText.setText(String.format("Welcome %s", account.getName()));
        balanceText.setText(formatRupiah.format(account.getBalance()));

        adapter = new TransactionAdapter(account.getTransactions(), this);
        transactionsView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        transactionsView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            session.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTransactionClicked(int index, Transaction item) {
        Intent intent = new Intent(this, SaveActivity.class);
        intent.putExtra(TRANSACTION_KEY, item);
        intent.putExtra(INDEX_KEY, 0);
        startActivityForResult(intent, UPDATE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Transaction transaction = data.getParcelableExtra(TRANSACTION_KEY);
            if (requestCode == INSERT_REQUEST) {
                account.addTransaction(transaction);
            } else if (requestCode == UPDATE_REQUEST) {
                int index = data.getIntExtra(INDEX_KEY, 0);
                account.updateTransaction(index, transaction);
            }
            adapter.notifyDataSetChanged();
            balanceText.setText(formatRupiah.format(account.getBalance()));
        }
    }

    public void handleLogout(MenuItem item) {
        Intent intent = new Intent(this, LoginActivity.class);
        session.logout();
        startActivity(intent);

    }
}

