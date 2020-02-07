package pontes.mars.com.ifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pontes.mars.com.ifood.R;
import pontes.mars.com.ifood.adapter.AdapterProduto;
import pontes.mars.com.ifood.helper.ConfiguracaoFirebase;
import pontes.mars.com.ifood.helper.UsuarioFirebase;
import pontes.mars.com.ifood.listener.RecyclerItemClickListener;
import pontes.mars.com.ifood.model.Produto;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recylerProduto;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        //Configurações iniciais
        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdIsuario();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("iFood - empresa");
        setSupportActionBar(toolbar);

        //Configurar o recyclerView
        recylerProduto.setLayoutManager(new LinearLayoutManager(this));
        recylerProduto.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recylerProduto.setAdapter(adapterProduto);

        //Recuperar produtos da empresa
        recuperarProdutos();

        //Adicionar evento de clique no recyclerview
        recylerProduto.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recylerProduto,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {



                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {

                        alertaMensagem(position);

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

    }//fim do onCreate

    private void recuperarProdutos(){

        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child(idUsuarioLogado);

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterProduto.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void inicializarComponentes(){
        recylerProduto = findViewById(R.id.recyclerProdutos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate (R.menu.menu_empresa, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                break;

            case R.id.menuConfigurações:
                abrirConfiguracoes();
                break;

            case R.id.menuNovoProduto:
                abrirNovoProduto();
                break;

            case R.id.menuPedidos:
                abrirPedidos();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){

        try {
            autenticacao.signOut();
            finish();
            startActivity(new Intent(EmpresaActivity.this, AutenticacaoActivity.class));
        }catch (Exception e){
            e.printStackTrace();
        }
    }//deslogarUsuario

    private void abrirPedidos(){
        startActivity(new Intent(EmpresaActivity.this, PedidosActivity.class));
    }//fim do abrirPedidos

    private void abrirConfiguracoes(){
        startActivity(new Intent(EmpresaActivity.this, ConfiguracoesEmpresaActivity.class));
    }//fim do abrirConfiguracoes

    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }//fim do abrirNovoProduto

    public void alertaMensagem(final int position){

        AlertDialog.Builder dialogScan = new AlertDialog.Builder(this);
        dialogScan.setTitle("Aviso: ");
        dialogScan.setMessage("Deseja excluir o produto?");
        dialogScan.setCancelable(false);
        dialogScan.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Produto produtoSelecionado = produtos.get(position);
                produtoSelecionado.remover();
                Toast.makeText(EmpresaActivity.this, "Produto excluído com sucesso!",
                        Toast.LENGTH_SHORT).show();
                produtos.clear();

            }
        });
        dialogScan.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = dialogScan.create();
        dialog.show();

    }




}//fim da classe
