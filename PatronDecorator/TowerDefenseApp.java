import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Aplicación de Torre de Defensa estilo "Grow Castle" que demuestra el Patrón Decorator.
 *
 * Contiene:
 * - Dominio: Interfaz Tower, BaseTower, TowerDecorator y 4 decoradores concretos
 * - Infraestructura: Servidor HTTP embebido con API REST
 * - Presentación: Frontend HTML5 Canvas con bucle de juego a 60 FPS
 *
 * Compilar y ejecutar:
 *   javac TowerDefenseApp.java
 *   java TowerDefenseApp
 *   Abrir http://localhost:8080 en el navegador.
 */
public class TowerDefenseApp {

    // =========================================================================
    // CAPA DE DOMINIO — Patrón Decorator
    // =========================================================================

    /**
     * Interfaz Componente: contrato base para todas las torres.
     * Define los comportamientos y estadísticas que pueden ser modificados por decoradores.
     */
    interface Tower {
        /** Retorna el daño base por proyectil */
        int getDamage();
        /** Retorna la velocidad de ataque en disparos por segundo */
        double getAttackSpeed();
        /** Retorna la capacidad de escudo defensivo */
        int getShieldCapacity();
        /** Retorna el oro generado pasivamente por oleada */
        int getGoldPerWave();
        /** Retorna una descripción legible de la torre con todas sus mejoras */
        String getDescription();
        /** Indica si la torre tiene efecto de congelamiento */
        boolean hasFreezeEffect();
    }

    /**
     * Componente Concreto: torre base con estadísticas iniciales.
     * Es el punto de partida antes de aplicar cualquier decorador.
     */
    static class BaseTower implements Tower {
        @Override public int getDamage() { return 10; }
        @Override public double getAttackSpeed() { return 1.5; }
        @Override public int getShieldCapacity() { return 0; }
        @Override public int getGoldPerWave() { return 0; }
        @Override public String getDescription() { return "Base Tower"; }
        @Override public boolean hasFreezeEffect() { return false; }
    }

    /**
     * Decorador Base Abstracto: implementa Tower y delega al componente envuelto.
     * Los decoradores concretos extienden esta clase y sobreescriben solo lo necesario.
     */
    static abstract class TowerDecorator implements Tower {
        /** Referencia al objeto Tower envuelto */
        protected final Tower wrapped;
        TowerDecorator(Tower wrapped) { this.wrapped = wrapped; }
        @Override public int getDamage() { return wrapped.getDamage(); }
        @Override public double getAttackSpeed() { return wrapped.getAttackSpeed(); }
        @Override public int getShieldCapacity() { return wrapped.getShieldCapacity(); }
        @Override public int getGoldPerWave() { return wrapped.getGoldPerWave(); }
        @Override public String getDescription() { return wrapped.getDescription(); }
        @Override public boolean hasFreezeEffect() { return wrapped.hasFreezeEffect(); }
    }

    /**
     * Decorador Concreto: incrementa la velocidad de ataque.
     * Cada capa añade +0.8 disparos por segundo, apilable.
     */
    static class FastFireDecorator extends TowerDecorator {
        private static final double SPEED_BONUS = 0.8;
        FastFireDecorator(Tower wrapped) { super(wrapped); }
        @Override public double getAttackSpeed() { return wrapped.getAttackSpeed() + SPEED_BONUS; }
        @Override public String getDescription() { return wrapped.getDescription() + " + FastFire"; }
    }

    /**
     * Decorador Concreto: activa el efecto de congelamiento en los proyectiles.
     * Los proyectiles se vuelven azules y reducen la velocidad del enemigo al impactar.
     */
    static class FreezeDecorator extends TowerDecorator {
        FreezeDecorator(Tower wrapped) { super(wrapped); }
        @Override public boolean hasFreezeEffect() { return true; }
        @Override public int getDamage() { return wrapped.getDamage() + 3; }
        @Override public String getDescription() { return wrapped.getDescription() + " + Freeze"; }
    }

    /**
     * Decorador Concreto: añade capacidad de escudo defensivo.
     * Cada capa suma +30 puntos de escudo, apilable.
     */
    static class ShieldDecorator extends TowerDecorator {
        private static final int SHIELD_BONUS = 30;
        ShieldDecorator(Tower wrapped) { super(wrapped); }
        @Override public int getShieldCapacity() { return wrapped.getShieldCapacity() + SHIELD_BONUS; }
        @Override public String getDescription() { return wrapped.getDescription() + " + Shield"; }
    }

    /**
     * Decorador Concreto: genera oro pasivamente por cada oleada.
     * Cada capa suma +20 de oro por oleada, apilable.
     */
    static class GoldGeneratorDecorator extends TowerDecorator {
        private static final int GOLD_BONUS = 20;
        GoldGeneratorDecorator(Tower wrapped) { super(wrapped); }
        @Override public int getGoldPerWave() { return wrapped.getGoldPerWave() + GOLD_BONUS; }
        @Override public String getDescription() { return wrapped.getDescription() + " + GoldGen"; }
    }

    // =========================================================================
    // CAPA DE INFRAESTRUCTURA — Servidor HTTP y Estado Global
    // =========================================================================

    /** Torre actual — fuente única de verdad del estado del juego */
    private static Tower currentTower = new BaseTower();

    /**
     * Punto de entrada principal. Arranca el servidor HTTP embebido en el puerto 8080.
     */
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", TowerDefenseApp::handleRoot);
        server.createContext("/api/game", TowerDefenseApp::handleApi);
        server.setExecutor(null);
        server.start();
        System.out.println("=====================================================");
        System.out.println("  Grow Castle — Decorator Pattern Tower Defense");
        System.out.println("  Servidor iniciado en http://localhost:" + port);
        System.out.println("=====================================================");
    }

    /** Sirve la página HTML principal */
    private static void handleRoot(HttpExchange ex) throws IOException {
        byte[] b = HTML.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html;charset=UTF-8");
        ex.sendResponseHeaders(200, b.length);
        try (OutputStream o = ex.getResponseBody()) { o.write(b); }
    }

    /** Maneja las peticiones a la API del juego */
    private static void handleApi(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "application/json;charset=UTF-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        String q = ex.getRequestURI().getQuery();
        String action = (q != null && q.startsWith("action=")) ? q.substring(7) : "";

        /* Aplicar el decorador correspondiente según la acción solicitada */
        switch (action) {
            case "addFastFire": currentTower = new FastFireDecorator(currentTower); break;
            case "addFreeze":   currentTower = new FreezeDecorator(currentTower); break;
            case "addShield":   currentTower = new ShieldDecorator(currentTower); break;
            case "addGold":     currentTower = new GoldGeneratorDecorator(currentTower); break;
            case "reset":       currentTower = new BaseTower(); break;
            case "status":      break;
            default:            break;
        }

        /* Usar Locale.US para asegurar punto decimal en JSON (evita comas en locales hispanos) */
        String json = "{" +
            "\"damage\":" + currentTower.getDamage() +
            ",\"attackSpeed\":" + String.format(Locale.US, "%.1f", currentTower.getAttackSpeed()) +
            ",\"shieldCapacity\":" + currentTower.getShieldCapacity() +
            ",\"goldPerWave\":" + currentTower.getGoldPerWave() +
            ",\"description\":\"" + esc(currentTower.getDescription()) + "\"" +
            ",\"hasFreezeEffect\":" + currentTower.hasFreezeEffect() +
            "}";
        byte[] b = json.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(200, b.length);
        try (OutputStream o = ex.getResponseBody()) { o.write(b); }
    }

    /** Escapa caracteres para inserción segura en JSON */
    private static String esc(String s) {
        return s.replace("\\","\\\\").replace("\"","\\\"");
    }

    // =========================================================================
    // CAPA DE PRESENTACIÓN — Frontend HTML5 Canvas embebido
    // =========================================================================

    /**
     * Contenido HTML completo del frontend con motor de juego Canvas.
     * Incluye bucle de juego a 60 FPS, sistema de enemigos, proyectiles,
     * detección de colisiones AABB, y renderizado visual estilo Grow Castle.
     */
    private static final String HTML =
"<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>\n" +
"<meta name='viewport' content='width=device-width,initial-scale=1.0'>\n" +
"<title>Grow Castle — Decorator Pattern Demo</title>\n" +
"<meta name='description' content='Tower Defense game demonstrating the Decorator Design Pattern'>\n" +
"<link href='https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;900&display=swap' rel='stylesheet'>\n" +
"<style>\n" +
"*{margin:0;padding:0;box-sizing:border-box}\n" +
"body{background:#0b0f1e;font-family:'Inter',sans-serif;color:#e2e8f0;display:flex;flex-direction:column;align-items:center;min-height:100vh}\n" +
"h1{font-size:1.8rem;font-weight:900;padding:1.2rem 0 .2rem;background:linear-gradient(135deg,#6366f1,#22d3ee);-webkit-background-clip:text;-webkit-text-fill-color:transparent}\n" +
".sub{font-size:.75rem;color:#64748b;margin-bottom:.8rem}\n" +
"canvas{border:1px solid rgba(255,255,255,.08);border-radius:12px;display:block;background:#111827}\n" +
".panel{display:flex;flex-wrap:wrap;gap:.5rem;justify-content:center;margin:.8rem 0;max-width:820px}\n" +
"button{font-family:'Inter',sans-serif;font-weight:600;font-size:.78rem;padding:.55rem 1rem;border:1px solid rgba(255,255,255,.1);border-radius:8px;cursor:pointer;background:rgba(255,255,255,.05);color:#e2e8f0;transition:all .2s}\n" +
"button:hover{transform:translateY(-1px);border-color:#6366f1;box-shadow:0 4px 15px rgba(99,102,241,.35)}\n" +
"button:active{transform:scale(.97)}\n" +
"button.fire{border-color:#f59e0b;color:#f59e0b}button.fire:hover{background:rgba(245,158,11,.12);box-shadow:0 4px 15px rgba(245,158,11,.3)}\n" +
"button.ice{border-color:#22d3ee;color:#22d3ee}button.ice:hover{background:rgba(34,211,238,.12);box-shadow:0 4px 15px rgba(34,211,238,.3)}\n" +
"button.shield{border-color:#6366f1;color:#6366f1}button.shield:hover{background:rgba(99,102,241,.12)}\n" +
"button.gold{border-color:#10b981;color:#10b981}button.gold:hover{background:rgba(16,185,129,.12);box-shadow:0 4px 15px rgba(16,185,129,.3)}\n" +
"button.reset{border-color:#ef4444;color:#ef4444}button.reset:hover{background:rgba(239,68,68,.1)}\n" +
".desc{font-size:.7rem;color:#94a3b8;text-align:center;max-width:820px;padding:.4rem;background:rgba(255,255,255,.03);border-radius:6px;border:1px solid rgba(255,255,255,.06)}\n" +
"</style></head><body>\n" +
"<h1>Grow Castle</h1><p class='sub'>Decorator Design Pattern — Tower Defense</p>\n" +
"<canvas id='gc' width='800' height='450'></canvas>\n" +
"<div class='panel'>\n" +
"<button class='fire' onclick=\"buy('addFastFire')\">🔥 Fast Fire (+0.8 spd)</button>\n" +
"<button class='ice' onclick=\"buy('addFreeze')\">❄️ Freeze (slow enemies)</button>\n" +
"<button class='shield' onclick=\"buy('addShield')\">🛡️ Shield (+30 HP)</button>\n" +
"<button class='gold' onclick=\"buy('addGold')\">💰 Gold Gen (+20/wave)</button>\n" +
"<button class='reset' onclick=\"buy('reset')\">🔄 Reset</button>\n" +
"</div>\n" +
"<div class='desc' id='desc'>Base Tower</div>\n" +
"<script>\n" +
"const cv=document.getElementById('gc'),ctx=cv.getContext('2d');\n" +
"/* Estado del juego sincronizado con el backend */\n" +
"let S={damage:10,attackSpeed:1.5,shieldCapacity:0,goldPerWave:0,hasFreezeEffect:false,description:'Base Tower'};\n" +
"let enemies=[],projectiles=[],particles=[];\n" +
"let gold=50,score=0,wave=0,waveTimer=0,spawnTimer=0,shootTimer=0;\n" +
"let towerHP=100,towerMaxHP=100,shieldHP=0;\n" +
"const TW=60,TH=90,TX=80,GROUND=cv.height-60;\n" +
"\n" +
"/* Comunicación con la API del backend para aplicar decoradores */\n" +
"async function buy(action){\n" +
"  const r=await fetch('/api/game?action='+action);\n" +
"  const d=await r.json();\n" +
"  S=d;\n" +
"  if(action==='reset'){shieldHP=0;}\n" +
"  else if(action==='addShield'){shieldHP=S.shieldCapacity;}\n" +
"  document.getElementById('desc').textContent=S.description;\n" +
"}\n" +
"\n" +
"/* Fábrica de enemigos con estadísticas escaladas por oleada */\n" +
"function spawnEnemy(){\n" +
"  let hp=20+wave*8;\n" +
"  let speed=1.2+Math.random()*0.6+wave*0.05;\n" +
"  let size=18+Math.random()*10;\n" +
"  enemies.push({x:cv.width+10,y:GROUND-size,w:size,h:size,hp:hp,maxHp:hp,speed:speed,baseSpeed:speed,frozen:false,frozenTimer:0,color:'#ef4444'});\n" +
"}\n" +
"\n" +
"/* Crear un proyectil dirigido al enemigo más cercano */\n" +
"function shoot(){\n" +
"  if(enemies.length===0)return;\n" +
"  let nearest=enemies.reduce((a,b)=>a.x<b.x?a:b);\n" +
"  let sx=TX+TW,sy=GROUND-TH+10;\n" +
"  let dx=nearest.x+nearest.w/2-sx,dy=nearest.y+nearest.h/2-sy;\n" +
"  let dist=Math.sqrt(dx*dx+dy*dy);\n" +
"  if(dist===0)return;\n" +
"  let spd=7;\n" +
"  projectiles.push({x:sx,y:sy,vx:dx/dist*spd,vy:dy/dist*spd,damage:S.damage,freeze:S.hasFreezeEffect});\n" +
"}\n" +
"\n" +
"/* Detección de colisiones AABB entre proyectiles y enemigos */\n" +
"function checkCollisions(){\n" +
"  for(let i=projectiles.length-1;i>=0;i--){\n" +
"    let p=projectiles[i];\n" +
"    for(let j=enemies.length-1;j>=0;j--){\n" +
"      let e=enemies[j];\n" +
"      /* Verificación AABB: si el proyectil (4x4) intersecta al enemigo */\n" +
"      if(p.x<e.x+e.w&&p.x+4>e.x&&p.y<e.y+e.h&&p.y+4>e.y){\n" +
"        e.hp-=p.damage;\n" +
"        /* Si el proyectil tiene efecto de congelamiento, reducir velocidad */\n" +
"        if(p.freeze){e.frozen=true;e.frozenTimer=120;e.speed=e.baseSpeed*0.5;e.color='#22d3ee';}\n" +
"        /* Generar partículas de impacto */\n" +
"        for(let k=0;k<6;k++)particles.push({x:p.x,y:p.y,vx:(Math.random()-.5)*3,vy:(Math.random()-.5)*3,life:20,color:p.freeze?'#22d3ee':'#f59e0b'});\n" +
"        projectiles.splice(i,1);\n" +
"        if(e.hp<=0){\n" +
"          score+=10;gold+=5;\n" +
"          /* Partículas de destrucción del enemigo */\n" +
"          for(let k=0;k<12;k++)particles.push({x:e.x+e.w/2,y:e.y+e.h/2,vx:(Math.random()-.5)*5,vy:(Math.random()-.5)*5,life:30,color:e.frozen?'#22d3ee':'#ef4444'});\n" +
"          enemies.splice(j,1);\n" +
"        }\n" +
"        break;\n" +
"      }\n" +
"    }\n" +
"  }\n" +
"}\n" +
"\n" +
"/* Verificar si un enemigo alcanzó la torre */\n" +
"function checkTowerDamage(){\n" +
"  for(let i=enemies.length-1;i>=0;i--){\n" +
"    if(enemies[i].x<=TX+TW){\n" +
"      let dmg=5;\n" +
"      if(shieldHP>0){let absorbed=Math.min(shieldHP,dmg);shieldHP-=absorbed;dmg-=absorbed;}\n" +
"      towerHP-=dmg;\n" +
"      enemies.splice(i,1);\n" +
"      if(towerHP<=0){towerHP=0;}\n" +
"    }\n" +
"  }\n" +
"}\n" +
"\n" +
"/* Actualizar estado de congelamiento de enemigos */\n" +
"function updateFrozen(){\n" +
"  enemies.forEach(e=>{\n" +
"    if(e.frozen){e.frozenTimer--;if(e.frozenTimer<=0){e.frozen=false;e.speed=e.baseSpeed;e.color='#ef4444';}}\n" +
"  });\n" +
"}\n" +
"\n" +
"/* ===================== RENDERIZADO ===================== */\n" +
"\n" +
"/* Dibujar el cielo nocturno con estrellas y gradiente */\n" +
"function drawBackground(){\n" +
"  let grd=ctx.createLinearGradient(0,0,0,cv.height);\n" +
"  grd.addColorStop(0,'#0f172a');grd.addColorStop(0.7,'#1e293b');grd.addColorStop(1,'#334155');\n" +
"  ctx.fillStyle=grd;ctx.fillRect(0,0,cv.width,cv.height);\n" +
"  /* Estrellas decorativas */\n" +
"  ctx.fillStyle='rgba(255,255,255,0.4)';\n" +
"  for(let i=0;i<30;i++){let sx=((i*137+51)%800),sy=((i*97+23)%250);ctx.fillRect(sx,sy,1.5,1.5);}\n" +
"  /* Suelo con degradado */\n" +
"  let gg=ctx.createLinearGradient(0,GROUND,0,cv.height);\n" +
"  gg.addColorStop(0,'#1e3a1e');gg.addColorStop(1,'#0f1f0f');\n" +
"  ctx.fillStyle=gg;ctx.fillRect(0,GROUND,cv.width,cv.height-GROUND);\n" +
"  ctx.fillStyle='#2d5a2d';ctx.fillRect(0,GROUND,cv.width,2);\n" +
"}\n" +
"\n" +
"/* Dibujar la torre con indicador visual de escudo */\n" +
"function drawTower(){\n" +
"  let bx=TX,by=GROUND-TH;\n" +
"  /* Cuerpo de la torre */\n" +
"  ctx.fillStyle='#64748b';ctx.fillRect(bx,by+TH*0.3,TW,TH*0.7);\n" +
"  ctx.fillStyle='#475569';ctx.fillRect(bx+5,by+TH*0.4,12,20);ctx.fillRect(bx+22,by+TH*0.4,12,20);ctx.fillRect(bx+39,by+TH*0.4,12,20);\n" +
"  /* Parte superior / almenas */\n" +
"  ctx.fillStyle='#94a3b8';\n" +
"  ctx.fillRect(bx-5,by+TH*0.2,TW+10,TH*0.12);\n" +
"  for(let i=0;i<4;i++)ctx.fillRect(bx-5+i*18,by,14,TH*0.22);\n" +
"  /* Barra de vida de la torre */\n" +
"  ctx.fillStyle='#1e293b';ctx.fillRect(bx-5,by-14,TW+10,8);\n" +
"  ctx.fillStyle=towerHP>50?'#10b981':'#ef4444';ctx.fillRect(bx-5,by-14,(TW+10)*(towerHP/towerMaxHP),8);\n" +
"  /* Aura de escudo translúcida (si está activo) */\n" +
"  if(S.shieldCapacity>0&&shieldHP>0){\n" +
"    ctx.save();ctx.globalAlpha=0.15+0.05*Math.sin(Date.now()/300);\n" +
"    ctx.strokeStyle='#6366f1';ctx.lineWidth=3;\n" +
"    ctx.beginPath();ctx.ellipse(bx+TW/2,by+TH/2,TW*0.8,TH*0.7,0,0,Math.PI*2);ctx.stroke();\n" +
"    ctx.fillStyle='#6366f1';ctx.fill();ctx.restore();\n" +
"    /* Barra de escudo */\n" +
"    ctx.fillStyle='#1e293b';ctx.fillRect(bx-5,by-24,TW+10,8);\n" +
"    ctx.fillStyle='#6366f1';ctx.fillRect(bx-5,by-24,(TW+10)*(shieldHP/S.shieldCapacity),8);\n" +
"  }\n" +
"}\n" +
"\n" +
"/* Dibujar enemigos con barra de vida */\n" +
"function drawEnemies(){\n" +
"  enemies.forEach(e=>{\n" +
"    ctx.fillStyle=e.color;ctx.fillRect(e.x,e.y,e.w,e.h);\n" +
"    /* Ojos del enemigo */\n" +
"    ctx.fillStyle='#fff';ctx.fillRect(e.x+3,e.y+4,4,4);ctx.fillRect(e.x+e.w-7,e.y+4,4,4);\n" +
"    ctx.fillStyle='#000';ctx.fillRect(e.x+3,e.y+5,2,2);ctx.fillRect(e.x+e.w-5,e.y+5,2,2);\n" +
"    /* Barra de vida */\n" +
"    ctx.fillStyle='#1e293b';ctx.fillRect(e.x,e.y-6,e.w,4);\n" +
"    ctx.fillStyle=e.frozen?'#22d3ee':'#10b981';ctx.fillRect(e.x,e.y-6,e.w*(e.hp/e.maxHp),4);\n" +
"    /* Indicador visual de congelamiento */\n" +
"    if(e.frozen){ctx.fillStyle='rgba(34,211,238,0.3)';ctx.fillRect(e.x-2,e.y-2,e.w+4,e.h+4);}\n" +
"  });\n" +
"}\n" +
"\n" +
"/* Dibujar proyectiles — azules si son de hielo, dorados si son normales */\n" +
"function drawProjectiles(){\n" +
"  projectiles.forEach(p=>{\n" +
"    ctx.fillStyle=p.freeze?'#22d3ee':'#f59e0b';\n" +
"    ctx.beginPath();ctx.arc(p.x,p.y,3,0,Math.PI*2);ctx.fill();\n" +
"    /* Estela del proyectil */\n" +
"    ctx.fillStyle=p.freeze?'rgba(34,211,238,0.3)':'rgba(245,158,11,0.3)';\n" +
"    ctx.beginPath();ctx.arc(p.x-p.vx,p.y-p.vy,2,0,Math.PI*2);ctx.fill();\n" +
"  });\n" +
"}\n" +
"\n" +
"/* Dibujar partículas de efectos */\n" +
"function drawParticles(){\n" +
"  particles.forEach(p=>{ctx.globalAlpha=p.life/30;ctx.fillStyle=p.color;ctx.fillRect(p.x,p.y,3,3);});\n" +
"  ctx.globalAlpha=1;\n" +
"}\n" +
"\n" +
"/* Interfaz de usuario superpuesta en el canvas */\n" +
"function drawHUD(){\n" +
"  ctx.fillStyle='rgba(0,0,0,0.5)';ctx.fillRect(0,0,cv.width,32);\n" +
"  ctx.font='bold 13px Inter,sans-serif';ctx.textBaseline='middle';\n" +
"  ctx.fillStyle='#f59e0b';ctx.fillText('💰 Gold: '+gold,10,16);\n" +
"  ctx.fillStyle='#6366f1';ctx.fillText('🛡️ Shield: '+shieldHP,140,16);\n" +
"  ctx.fillStyle='#ef4444';ctx.fillText('⚔️ DMG: '+S.damage,290,16);\n" +
"  ctx.fillStyle='#22d3ee';ctx.fillText('⚡ SPD: '+S.attackSpeed,410,16);\n" +
"  ctx.fillStyle='#10b981';ctx.fillText('🏆 Score: '+score,540,16);\n" +
"  ctx.fillStyle='#a855f7';ctx.fillText('🌊 Wave: '+wave,670,16);\n" +
"  /* Indicador de Game Over */\n" +
"  if(towerHP<=0){\n" +
"    ctx.fillStyle='rgba(0,0,0,0.7)';ctx.fillRect(0,0,cv.width,cv.height);\n" +
"    ctx.fillStyle='#ef4444';ctx.font='bold 48px Inter';ctx.textAlign='center';\n" +
"    ctx.fillText('GAME OVER',cv.width/2,cv.height/2-20);\n" +
"    ctx.fillStyle='#94a3b8';ctx.font='16px Inter';\n" +
"    ctx.fillText('Score: '+score+' | Waves survived: '+wave,cv.width/2,cv.height/2+25);\n" +
"    ctx.fillText('Click Reset to play again',cv.width/2,cv.height/2+55);\n" +
"    ctx.textAlign='start';\n" +
"  }\n" +
"}\n" +
"\n" +
"/* ===================== BUCLE PRINCIPAL ===================== */\n" +
"\n" +
"function gameLoop(){\n" +
"  if(towerHP>0){\n" +
"    /* Generar oleadas automáticamente cada 600 frames (~10 seg) */\n" +
"    waveTimer++;\n" +
"    if(waveTimer>=600){waveTimer=0;wave++;gold+=S.goldPerWave;shieldHP=S.shieldCapacity;}\n" +
"    /* Generar enemigos periódicamente durante la oleada */\n" +
"    spawnTimer++;\n" +
"    let spawnRate=Math.max(40,120-wave*5);\n" +
"    if(spawnTimer>=spawnRate){spawnTimer=0;spawnEnemy();}\n" +
"    /* La torre dispara según su velocidad de ataque */\n" +
"    shootTimer++;\n" +
"    let fireInterval=Math.max(5,Math.floor(60/S.attackSpeed));\n" +
"    if(shootTimer>=fireInterval){shootTimer=0;shoot();}\n" +
"    /* Mover enemigos hacia la torre */\n" +
"    enemies.forEach(e=>{e.x-=e.speed;});\n" +
"    /* Mover proyectiles */\n" +
"    projectiles.forEach(p=>{p.x+=p.vx;p.y+=p.vy;});\n" +
"    /* Eliminar proyectiles fuera de pantalla */\n" +
"    projectiles=projectiles.filter(p=>p.x>0&&p.x<cv.width&&p.y>0&&p.y<cv.height);\n" +
"    /* Actualizar partículas */\n" +
"    particles.forEach(p=>{p.x+=p.vx;p.y+=p.vy;p.life--;});\n" +
"    particles=particles.filter(p=>p.life>0);\n" +
"    updateFrozen();\n" +
"    checkCollisions();\n" +
"    checkTowerDamage();\n" +
"  }\n" +
"  /* Renderizar la escena completa */\n" +
"  drawBackground();\n" +
"  drawTower();\n" +
"  drawEnemies();\n" +
"  drawProjectiles();\n" +
"  drawParticles();\n" +
"  drawHUD();\n" +
"  requestAnimationFrame(gameLoop);\n" +
"}\n" +
"\n" +
"/* Iniciar el bucle del juego al cargar la página */\n" +
"buy('status').then(()=>gameLoop());\n" +
"</script></body></html>"
;
}
