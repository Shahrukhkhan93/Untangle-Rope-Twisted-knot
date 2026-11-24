from pathlib import Path
from playwright.sync_api import sync_playwright
import json,re,hashlib
root=Path(__file__).parent; s=(root/'app/src/main/assets/index.html').read_text(); pat=r'const levels=(\[.*?\]);\s*const chapters='; raw=re.search(pat,s,re.S)[1];assert hashlib.sha256(raw.encode()).hexdigest()=='615db571d7650f06e6641faaae1be4aa127faceb6f97880b96bccd59fb07ee41' and len(json.loads(raw))==500
out={'level_definitions_byte_identical':True,'levels_sha256':hashlib.sha256(raw.encode()).hexdigest(),'actual_drag':[],'resize_state_preserved':[],'visual_checks':[]}
with sync_playwright() as pw:
 b=pw.chromium.launch(executable_path='/usr/bin/chromium',headless=True,args=['--no-sandbox','--disable-dev-shm-usage']);pg=b.new_page(viewport={'width':390,'height':780});errors=[];pg.on('pageerror',lambda e:errors.append(str(e)));pg.set_content(s,wait_until='domcontentloaded');pg.wait_for_function("!document.querySelector('#oceanStartup')",timeout=15000)
 def state():return pg.evaluate('__ropeTest.state()')
 def drag(a,z):
  r=pg.locator('#board').bounding_box();cw=pg.locator('#board').evaluate('(e)=>e.width/2');ch=520
  def pt(h):return r['x']+(35+70*(h%6)*(cw-70)/350)/cw*r['width'],r['y']+(78+73*(h//6))/ch*r['height']
  x,y=pt(a);xx,yy=pt(z);pg.mouse.move(x,y);pg.mouse.down();pg.mouse.move(xx,yy,steps=35);pg.mouse.up();pg.wait_for_timeout(500)
 for w,h in [(390,780),(320,480),(768,1024),(1024,1366),(280,480),(844,390)]:
  pg.set_viewport_size({'width':w,'height':h});pg.wait_for_timeout(200);pg.evaluate('__ropeTest.chooseLevel(500)');pg.wait_for_timeout(250);pg.screenshot(path=str(root/f'preview-{w}x{h}.png'),full_page=True);out['visual_checks'].append([w,h])
 for w,h in [(320,568),(768,1024),(1024,1366),(390,780)]:
  pg.set_viewport_size({'width':w,'height':h});pg.evaluate('__ropeTest.chooseLevel(1)');pg.wait_for_timeout(180);drag(9,20);pg.wait_for_function('__ropeTest.state().win',timeout=8000);out['actual_drag'].append({'viewport':[w,h],'level':1,'win':True});pg.wait_for_timeout(300);rr=pg.locator('#winNext').bounding_box();assert rr['x']>=0 and rr['y']>=0 and rr['x']+rr['width']<=w+1 and rr['y']+rr['height']<=h+1;pg.locator('#winNext').click();pg.wait_for_timeout(100)
 pg.evaluate('__ropeTest.chooseLevel(2)');pg.wait_for_timeout(200);drag(2,21);before=state();assert before['moves']==1
 for w,h in [(844,390),(768,1024),(360,640),(360,580),(1024,1366)]:
  pg.set_viewport_size({'width':w,'height':h});pg.wait_for_timeout(150);after=state();assert all(before[k]==after[k] for k in ['level','moves','ropeEndpoints','keys','crumbled','completed']);out['resize_state_preserved'].append([w,h])
 drag(26,9);pg.wait_for_function('__ropeTest.state().win',timeout=8000);out['level2_completed_after_resize']=True;out['js_errors']=errors;b.close()
(root/'portrait-resize-qa.json').write_text(json.dumps(out,indent=2));print(out,flush=True)
