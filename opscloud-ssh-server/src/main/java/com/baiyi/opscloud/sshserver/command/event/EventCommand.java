package com.baiyi.opscloud.sshserver.command.event;

import com.baiyi.opscloud.common.util.BeanCopierUtil;
import com.baiyi.opscloud.common.util.TimeUtil;
import com.baiyi.opscloud.domain.DataTable;
import com.baiyi.opscloud.domain.generator.opscloud.Event;
import com.baiyi.opscloud.domain.generator.opscloud.EventBusiness;
import com.baiyi.opscloud.domain.generator.opscloud.Server;
import com.baiyi.opscloud.domain.param.event.EventParam;
import com.baiyi.opscloud.domain.vo.server.ServerVO;
import com.baiyi.opscloud.event.IEventProcess;
import com.baiyi.opscloud.event.enums.EventTypeEnum;
import com.baiyi.opscloud.event.factory.EventFactory;
import com.baiyi.opscloud.service.event.EventBusinessService;
import com.baiyi.opscloud.sshcore.table.PrettyTable;
import com.baiyi.opscloud.sshserver.PromptColor;
import com.baiyi.opscloud.sshserver.annotation.CheckTerminalSize;
import com.baiyi.opscloud.sshserver.annotation.InvokeSessionUser;
import com.baiyi.opscloud.sshserver.annotation.ScreenClear;
import com.baiyi.opscloud.sshserver.command.component.SshShellComponent;
import com.baiyi.opscloud.sshserver.command.context.SessionCommandContext;
import com.baiyi.opscloud.sshserver.command.event.base.EventContext;
import com.baiyi.opscloud.sshserver.command.event.util.SeverityUtil;
import com.baiyi.opscloud.sshserver.command.server.base.BaseServerCommand;
import com.baiyi.opscloud.sshserver.command.util.ServerUtil;
import com.baiyi.opscloud.sshserver.util.ServerTableUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author baiyi
 * @Date 2021/10/13 3:19 下午
 * @Version 1.0
 */
@Slf4j
@SshShellComponent
@ShellCommandGroup("Event")
public class EventCommand extends BaseServerCommand {

    @Resource
    private EventBusinessService eventBusinessService;

    private Terminal terminal;

    @Autowired
    @Lazy
    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    protected static final int PAGE_FOOTER_SIZE = 6;

    @CheckTerminalSize(cols = 130, rows = 10)
    @ScreenClear
    @InvokeSessionUser(invokeAdmin = true)
    @ShellMethod(value = "查询事件", key = {"event", "list-event"})
    public void listEvent(@ShellOption(help = "Event Name", defaultValue = "") String name) {
        EventParam.UserPermissionEventPageQuery pageQuery = EventParam.UserPermissionEventPageQuery.builder()
                .name(name)
                .build();
        pageQuery.setPage(1);
        PrettyTable pt = PrettyTable
                .fieldNames("ID",
                        "Severity",
                        "Event Name",
                        "Server Name",
                        "IP",
                        "Lastchange Time",
                        "Accounts"
                );
        pageQuery.setUserId(com.baiyi.opscloud.common.util.SessionUtil.getIsAdmin() ? null : com.baiyi.opscloud.common.util.SessionUtil.getUserId());
        pageQuery.setLength(terminal.getSize().getRows() - PAGE_FOOTER_SIZE);

        IEventProcess iEventProcess = EventFactory.getIEventProcessByEventType(EventTypeEnum.ZABBIX_PROBLEM);
        DataTable<Event> table = iEventProcess.listEvent(pageQuery);
        Map<Integer, EventContext> eventMapper = Maps.newHashMap();
        int id = 1;
        for (Event event : table.getData()) {
            EventContext eventContext = BeanCopierUtil.copyProperties(event, EventContext.class);
            List<EventBusiness> eventBusinesses = eventBusinessService.queryByEventId(eventContext.getId());
            Server server = serverService.getById(eventBusinesses.get(0).getBusinessId());
            ServerVO.Server serverVO = sshServerPacker.wrapToVO(server);
            eventContext.setServerVO(serverVO);
            eventMapper.put(id, eventContext);
            pt.addRow(id,
                    SeverityUtil.toTerminalStr(eventContext.getPriority()),
                    eventContext.getEventName(),
                    serverVO.getDisplayName(),
                    ServerUtil.toDisplayIp(serverVO),
                    TimeUtil.toGmtDate(eventContext.getLastchangeTime()),
                    toAccountField(serverVO, com.baiyi.opscloud.common.util.SessionUtil.getIsAdmin())
            );
            id++;
        }
        SessionCommandContext.setEventMapper(eventMapper);
        helper.print(pt.toString());
        helper.print(ServerTableUtil.buildPagination(table.getTotalNum(),
                        pageQuery.getPage(),
                        pageQuery.getLength()),
                PromptColor.GREEN);
    }

}
